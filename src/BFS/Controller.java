package BFS;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.*;


/**
 * Created by E.E on 06.05.2017.
 */

public class Controller
{
    @FXML
    public AnchorPane root;

    public TextField eingabeFeld;

    public ComboBox comboBoxVON;
    public ComboBox comboBoxZU;
    public ComboBox startKnoten;
    public ComboBox löschComboBox;
    public ComboBox löschComboBoxKanten;

    public Button bfs2;
    public Button löschButton;
    public Button löschButtonKante;
    public Button reset;
    public Button dfs;

    public Button verbinden;
    public Button newKnoten;

    public Button test;

    int warteZeit;

    /**Warteschlange für die Knoten*/
    private Queue<Knoten> warteschlange;
    /**Listen für alle angelegten Knoten und Kanten*/
    private static ArrayList<Knoten> alleKnoten = new ArrayList<>();
    //private static ArrayList<Knoten> knotenZumFärben = new ArrayList<>();
    private static ArrayList<Kante> alleKanten = new ArrayList<>();

    private static int[][] globaleMatrix;

    public Controller()
    {
        warteschlange = new LinkedList<>();
    }

    /**Limitiert die Texteingabe bei Knoten-Bezeichnung*/
    public void addTextLimiter()
    {
        final int maxLänge = 4;
        eingabeFeld.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue)
            {
                if (eingabeFeld.getText().length() > maxLänge) {
                    String s = eingabeFeld.getText().substring(0, maxLänge);
                    eingabeFeld.setText(s);
                }
            }
        });
    }

    //ALT TEST-Animation
    public void handleButtonAction(ActionEvent e)
    {
        TranslateTransition circle1Animation = new TranslateTransition(Duration.seconds(1), alleKnoten.get(0));
        circle1Animation.setByY(150);

        TranslateTransition circle2Animation = new TranslateTransition(Duration.seconds(1), alleKnoten.get(1));
        circle2Animation.setByX(150);

        ParallelTransition animation = new ParallelTransition(circle1Animation, circle2Animation);

        animation.setAutoReverse(true);
        animation.setCycleCount(2);
        test.disableProperty().bind(animation.statusProperty().isEqualTo(Animation.Status.RUNNING)); //TODO evtl. später so machen
        test.setOnAction(a -> animation.play()); //TODO evtl. später so machen
    }

    /**Prüfe ob die Knoten-Wahl gültig ist und aktiviere die Buttons*/
    public void start()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        if(start != -1)
        {
            bfs2.setDisable(false);
            dfs.setDisable(false);
        }
    }

    /**Gewählter Knoten wird als Start-Knoten markiert und die Breitensuche gestartet*/
    public void bfsAusführen()
    {
        Knoten start = null;
        Object startComBox = startKnoten.getSelectionModel().getSelectedItem();
        bfsReset();
        for(Knoten n : alleKnoten)
        {
            if(n.bezeichnung == startComBox)
            {
                start = n;
                start.startKnoten = true;
            }
        }
        bfs(start);
    }

    /**Zurücksetzen einer Breitensuche*/
    public void bfsReset()
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            alleKnoten.get(i).besucht = false;
            alleKnoten.get(i).entfernungGesetzt = false;
            alleKnoten.get(i).entfernung = 0;
            alleKnoten.get(i).setFill(Color.WHITESMOKE);
            alleKnoten.get(i).startKnoten = false;
            alleKnoten.get(i).distanz.setText("");
        }
        for(Knoten n : alleKnoten)
        {
            n.setEffect(null);
        }
    }

    /**Gewählter Knoten wird als Start-Knoten markiert und die Tiefensuche gestartet*/
    public void dfsAusführen()
    {
        Knoten start = null;
        Object startComBox = startKnoten.getSelectionModel().getSelectedItem();
        for(Knoten n : alleKnoten)
        {
            if(n.bezeichnung == startComBox)
            {
                start = n;
            }
        }
        dfsReset();
        zeitStempel = 0;
        dfs(start);
        ausgabe(); //Zeitstempel TEST-Ausgabe in Shell

        //Animation für die Zeitstempel ablaufen lassen
        kantenAnimation();
        //erst die Stempel Animation abwarten, dann die Kanten färben
        PauseTransition pause = new PauseTransition(Duration.millis(warteZeit));
        pause.play();
        pause.setOnFinished((ActionEvent event) -> kantenKlassifizieren());
    }

    /**Zurücksetzen einer Tiefensuche*/
    private void dfsReset()
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            alleKnoten.get(i).besucht = false;
            alleKnoten.get(i).hinBesucht = false;
            alleKnoten.get(i).zurückBesucht = false;
            alleKnoten.get(i).stempelHin.setText("hin");
            alleKnoten.get(i).stempelZurück.setText("zurück");
            alleKnoten.get(i).zeitStempelHin = 0;
            alleKnoten.get(i).zeitStempelZurück = 0;
            alleKnoten.get(i).stempelHin.setEffect(null);
            alleKnoten.get(i).stempelZurück.setEffect(null);
        }
        for(Knoten n : alleKnoten)
        {
            for(Kante zu : n.kantenZuKnoten)
            {
                zu.setEffect(null);

                zu.linie.setStrokeWidth(1.2);
                zu.pfeil1.setEffect(null);
                zu.pfeil2.setEffect(null);
            }
            for(Kante von : n.kantenVonKnoten)
            {
                von.setEffect(null);

                von.linie.setStrokeWidth(1.2);
                von.linie.setStroke(Color.GRAY.deriveColor(1,1,0.5, 1));
                von.pfeil1.setStroke(Color.GRAY.deriveColor(1,1,0.5, 1));
                von.pfeil2.setStroke(Color.GRAY.deriveColor(1,1,0.5, 1));
            }
            for(Kante an : n.kantenAnKnoten)
            {
                an.setEffect(null);

                an.linie.setStrokeWidth(1.2);
                an.pfeil1.setEffect(null);
                an.pfeil2.setEffect(null);
            }
        }
        for(Kante m : alleKanten)
        {
            m.setEffect(null);
        }
    }

    /**Die Zeitstempel nach der Tiefensuche eintragen*/
    private void kantenAnimation()
    {
        DropShadow dsHin = new DropShadow(15, Color.RED);
        dsHin.setSpread(0.6);

        DropShadow dsZurück = new DropShadow(15, Color.BLUE);
        dsZurück.setSpread(0.6);

        int warten = 10;
        for(int i = 1; i <= alleKnoten.size()*2; i++)
        {
            for(Knoten knoten : alleKnoten)
            {
                if(i == knoten.zeitStempelHin)
                {
                    //gewisse Zeit abwarten und "hin"-Stempel eintragen
                    PauseTransition pause = new PauseTransition(Duration.millis(warten));
                    pause.play();
                    pause.setOnFinished((ActionEvent event) -> {
                        knoten.stempelHin.setText(Integer.toString(knoten.zeitStempelHin));
                        knoten.stempelHin.setEffect(dsHin);
                    });

                }
                else if(i == knoten.zeitStempelZurück)
                {
                    //gewisse Zeit abwarten und "zurück"-Stempel eintragen
                    PauseTransition pause = new PauseTransition(Duration.millis(warten));
                    pause.play();
                    pause.setOnFinished((ActionEvent event) -> {
                        knoten.stempelZurück.setText(Integer.toString(knoten.zeitStempelZurück));
                        knoten.stempelZurück.setEffect(dsZurück);
                    });
                }
            }
            warten = warten + 1000;
        }
        warteZeit = warten;
    }

    /**Kanten nach DFS Ablauf klassifizieren.*/
    private void kantenKlassifizieren()
    {
        for(Knoten n : alleKnoten)
        {
            for(Kante a : n.kantenVonKnoten)
            {
                for(Knoten m : alleKnoten)
                {
                    for(Kante b : m.kantenZuKnoten)
                    {
                        if(a.zu == b.zu && a.von == b.von)
                        {
                            //Vorwärts-/Baumkante
                            if( a.vonKnotenHinStempel < b.zuKnotenHinStempel & b.zuKnotenHinStempel < b.zuKnotenZurückStempel & b.zuKnotenZurückStempel < a.vonKnotenZurückStempel) //TODO Baum und Vor unterschediden
                            {
                                DropShadow ds = new DropShadow(10, Color.GREEN);
                                ds.setSpread(0.5);
                                a.linie.setStrokeWidth(2);
                                a.pfeil1.setEffect(ds);
                                a.pfeil2.setEffect(ds);
                                a.linie.setStroke(Color.GREEN.deriveColor(1,1,1, 1));
                                a.pfeil1.setStroke(Color.GREEN.deriveColor(1,1,1, 1));
                                a.pfeil2.setStroke(Color.GREEN.deriveColor(1,1,1, 1));
                            }
                            //Rückwärtskante
                            if( b.zuKnotenHinStempel < a.vonKnotenHinStempel & a.vonKnotenHinStempel < a.vonKnotenZurückStempel & a.vonKnotenZurückStempel < b.zuKnotenZurückStempel)
                            {
                                DropShadow ds = new DropShadow(10, Color.BLUE);
                                ds.setSpread(0.5);
                                a.linie.setStrokeWidth(2);
                                a.pfeil1.setEffect(ds);
                                a.pfeil2.setEffect(ds);
                                a.linie.setStroke(Color.BLUE.deriveColor(1,1,1, 1));
                                a.pfeil1.setStroke(Color.BLUE.deriveColor(1,1,1, 1));
                                a.pfeil2.setStroke(Color.BLUE.deriveColor(1,1,1, 1));
                            }
                            //Querkante
                            if( b.zuKnotenHinStempel < b.zuKnotenZurückStempel & b.zuKnotenZurückStempel < a.vonKnotenHinStempel & a.vonKnotenHinStempel < a.vonKnotenZurückStempel)
                            {
                                DropShadow ds = new DropShadow(10, Color.ORANGE);
                                ds.setSpread(0.5);
                                a.linie.setStrokeWidth(2);
                                a.pfeil1.setEffect(ds);
                                a.pfeil2.setEffect(ds);
                                a.linie.setStroke(Color.ORANGE.deriveColor(1,1,1, 1));
                                a.pfeil1.setStroke(Color.ORANGE.deriveColor(1,1,1, 1));
                                a.pfeil2.setStroke(Color.ORANGE.deriveColor(1,1,1, 1));
                            }
                            System.out.print("GEFUNDEN!\t"+"\n");
                        }
                    }
                }
            }
        }
    }

    /**Nimmt die Adjazenzmatrix und liefert alle Nachbarn von Knoten x*/
    public ArrayList<Knoten> findeNachbar(int[][] adjazenzmatrix, Knoten x)
    {
        int nodeIndex = -1;

        ArrayList<Knoten> nachbar = new ArrayList<>();
        for (int i = 0; i < alleKnoten.size(); i++)
        {
            if(alleKnoten.get(i).equals(x))
            {
                nodeIndex = i;
                break;
            }
        }
        if(nodeIndex != -1)
        {
            for (int j = 0; j < adjazenzmatrix[nodeIndex].length; j++)
            {
                if(adjazenzmatrix[nodeIndex][j] == 1)
                {
                    nachbar.add(alleKnoten.get(j));
                }
            }
        }
        return nachbar;
    }

    /**Breitensuche mit Start-Knoten*/
    public void bfs(Knoten startKnoten)
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            alleKnoten.get(i).besucht = false;
        }

        int knotenAnzahl = alleKnoten.size();
        int matrix[][] = new int[knotenAnzahl][knotenAnzahl];

        /**Alle Kanten durchlaufen und Verbindugen in Matrix setzen*/
        for(int i = 0; i < alleKanten.size(); i++)
        {
            try
            {
                matrix[alleKanten.get(i).von][alleKanten.get(i).zu] = 1;
            }catch (Exception e) {System.out.print ("Fehler bei Matrix setzen: " + e);
            }

        }
        //TESTAUSGABE: Matrix anschauen  //TODO löschen
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix.length; j++)
            {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n");
        }

        warteschlange.add(startKnoten);
        startKnoten.besucht = true;

        while (!warteschlange.isEmpty())
        {
            Knoten element = warteschlange.remove();

            ArrayList<Knoten> nachbarn = findeNachbar(matrix, element);
            for (int i = 0; i < nachbarn.size(); i++)
            {
                Knoten n = nachbarn.get(i);

                /**Wenn Knoten noch nicht besucht wurde und seine Entfernung noch nicht eingetragen ist
                 * Entfernung vom Nachbarn nehmen und einen Schritt dazu addieren*/
                if(!nachbarn.get(i).entfernungGesetzt && !n.besucht)
                {
                    nachbarn.get(i).entfernung = element.entfernung + 1;
                    nachbarn.get(i).entfernungGesetzt = true;
                }

                //knotenZumFärben.add(n);

                /**Knoten in die Warteschlange packen und als besucht markieren*/
                if(n != null && !n.besucht)
                {
                    warteschlange.add(n);
                    n.besucht = true;
                }
            }
            System.out.print("\n");
        }

        //Matrix kopieren für animationAblaufen
        globaleMatrix = matrix;

        //Drag bei allen Knoten deaktivieren.
        for(Knoten n : alleKnoten)
        {
            n.setMouseTransparent(true);
        }
        //Knoten nach Entfernung einfärben
        einfärben();
    }


    private void animationAblaufen(Knoten knoten, int pausenDauer, Color farbe)
    {
        ArrayList<Knoten> nachbarn = findeNachbar(globaleMatrix, knoten);
        for(Knoten nachbar : nachbarn)
        {
            if(nachbar.entfernung > knoten.entfernung)
            {
                //Suche Kante
                for(Kante k : alleKanten)
                {
                    if(k.von == knoten.id && k.zu == nachbar.id)
                    {
                        System.out.print("  k.von:\t"+k.von+"\t" + "k.zu:\t" +k.zu+ "\n");
                        System.out.print("  k.vonKnoten:\t"+k.vonKnoten+"\t" + "k.zuKnoten:\t" +k.zuKnoten+ "\n");

                        Circle circle = new Circle();
                        circle.setRadius(10);
                        circle.setFill(farbe);
                        circle.setStroke(Color.GRAY);
                        root.getChildren().add(circle);
                        circle.toBack();

                        PathElement[] path =
                                {
                                        new MoveTo(k.getStartX(), k.getStartY()),
                                        new LineTo(k.getEndX(), k.getEndY())
                                };

                        Path weg = new Path();
                        weg.getElements().addAll(path);

                        PathTransition animation = new PathTransition();
                        animation.setInterpolator(Interpolator.LINEAR);
                        animation.setDuration(new Duration(3600));
                        animation.setNode(circle);
                        animation.setPath(weg);

                        PauseTransition pause = new PauseTransition(Duration.millis(pausenDauer));
                        SequentialTransition sequence = new SequentialTransition (knoten, pause, animation);
                        sequence.play();

                        //Animation nach Ablauf entfernen
                        sequence.setOnFinished((ActionEvent event) -> {
                            root.getChildren().remove(animation.getNode());
                        });
                    }
                }
            }
        }
        //Warten bis die Animation abgelaufen ist, dann Knoten wieder "dragbar" machen
        PauseTransition pause = new PauseTransition(Duration.millis(pausenDauer+3600));
        pause.play();
        pause.setOnFinished((ActionEvent event) -> {
            knoten.setMouseTransparent(false);
            for(Knoten n : alleKnoten)
            {
                if(n.entfernung == 0)
                {
                    n.setMouseTransparent(false);
                }
            }
        });
    }

    FillTransition knotenEinfärben(Knoten knoten, int dauer, Color farbe)
    {
        DropShadow ds = new DropShadow();
        ds.setSpread(0.9);
        ds.setColor(Color.ANTIQUEWHITE);
        knoten.distanz.setEffect(ds);

        DropShadow leuchten = new DropShadow();
        leuchten.setWidth(55);
        leuchten.setHeight(15);
        leuchten.setColor(farbe);

        PauseTransition pause = new PauseTransition(Duration.millis(dauer));
        FillTransition flächeÜbergang = new FillTransition(); //Fläche
        flächeÜbergang.setShape(knoten);
        flächeÜbergang.setDuration(new Duration(1200));
        flächeÜbergang.setCycleCount(3);
        flächeÜbergang.setAutoReverse(true);
        SequentialTransition sequence = new SequentialTransition (knoten, pause, flächeÜbergang);
        sequence.play();
        sequence.setOnFinished( (ActionEvent event) -> {
            knoten.distanz.setText(String.valueOf(knoten.entfernung));
            knoten.setEffect(leuchten);
                }
        );
        return flächeÜbergang;
    }


    public void einfärben()
    {
        //StrokeTransition übergang = new StrokeTransition(); //Linie
        int dauer = 0;  //Nach welcher Dauer das Blinken beginnen soll, Blinkdauer immer 3600!
        int pausenDauer = 0;

        for (int i = 0; i < alleKnoten.size(); i++)
        {
            Knoten n = alleKnoten.get(i);

            switch(n.entfernung)
            {
                case 0:
                    Color farbe = Color.LIGHTYELLOW.deriveColor(1,1,1, 1);
                    //pausenDauer = 0;
                    if(n.startKnoten)
                    {
                        animationAblaufen(n, pausenDauer, farbe);  //hier werden die Farbkugeln abgeschoßen
                        FillTransition flächeÜbergang0 = knotenEinfärben(n, dauer, farbe);
                        flächeÜbergang0.setToValue(Color.LIGHTYELLOW.deriveColor(1,1,1, 1));
                    }
                    break;

                case 1:
                    Color farbe1 = Color.YELLOW.deriveColor(1,1,1, 1);
                    dauer = 0;
                    pausenDauer = 3600;
                    animationAblaufen(n, pausenDauer, farbe1);
                    FillTransition flächeÜbergang1 = knotenEinfärben(n, dauer, farbe1);
                    flächeÜbergang1.setToValue(Color.YELLOW.deriveColor(1,1,1, 1));
                    break;

                case 2:
                    Color farbe2 = Color.ORANGE.deriveColor(1,1,1, 1);
                    pausenDauer = 3600 * n.entfernung;
                    dauer = pausenDauer - 3600;
                    animationAblaufen(n, pausenDauer, farbe2);
                    FillTransition flächeÜbergang2 = knotenEinfärben(n, dauer, farbe2);
                    flächeÜbergang2.setToValue(Color.ORANGE.deriveColor(1,1,1, 1));
                    break;

                case 3:
                    Color farbe3 = Color.ORANGERED.deriveColor(1,1,1, 1);
                    pausenDauer = 3600 * n.entfernung;
                    dauer = pausenDauer - 3600;
                    animationAblaufen(n, pausenDauer, farbe3);
                    FillTransition flächeÜbergang3 = knotenEinfärben(n, dauer, farbe3);
                    flächeÜbergang3.setToValue(Color.ORANGERED.deriveColor(1,1,1, 1));
                    break;

                case 4:
                    Color farbe4 = Color.DEEPPINK.deriveColor(1,1,1, 1);
                    pausenDauer = 3600 * n.entfernung;
                    dauer = pausenDauer - 3600;
                    animationAblaufen(n, pausenDauer, farbe4);
                    FillTransition flächeÜbergang4 = knotenEinfärben(n, dauer, farbe4);
                    flächeÜbergang4.setToValue(Color.DEEPPINK.deriveColor(1,1,1, 1));
                    break;

                default:
                    Color farbeRest = Color.DARKVIOLET.deriveColor(1,1,1, 1);
                    pausenDauer = 3600 * n.entfernung;
                    dauer = pausenDauer - 3600;
                    animationAblaufen(n, pausenDauer, farbeRest);
                    FillTransition flächeÜbergangRest = knotenEinfärben(n, dauer, farbeRest);
                    flächeÜbergangRest.setToValue(Color.DARKVIOLET.deriveColor(1,1,1, 1));
                    break;
            }
        }
    }


    int zeitStempel;
    /**Tiefensuche*/
    public void dfs(Knoten startKnoten)
    {
        int knotenAnzahl = alleKnoten.size();
        int matrix[][] = new int[knotenAnzahl][knotenAnzahl];

        /**Alle Kanten durchlaufen und Verbindugen in Matrix setzen*/
        for(int i = 0; i < alleKanten.size(); i++)
        {
            System.out.print("alleKanten von: "+alleKanten.get(i).von+"   alleKanten zu "+ alleKanten.get(i).zu+ "\n");
            try
            {
                matrix[alleKanten.get(i).von][alleKanten.get(i).zu] = 1;
            }catch (Exception e) {System.out.print ("Fehler bei Matrix setzen: " + e);
            }
        }
        //TESTAUSGABE: Matrix anschauen
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix.length; j++)
            {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n");
        }

        zeitStempel++;

        //Zeitstempel für den Hinweg in beide Listen eintragen.
        for(Kante n : startKnoten.kantenVonKnoten)
        {
            n.vonKnotenHinStempel = zeitStempel;
        }
        for(Kante n : startKnoten.kantenZuKnoten)
        {
            n.zuKnotenHinStempel = zeitStempel;
        }

        startKnoten.zeitStempelHin = zeitStempel;
        startKnoten.hinBesucht = true;
        ArrayList<Knoten> nachbarn = findeNachbar(matrix, startKnoten);

        for (int i = 0; i < nachbarn.size(); i++)
        {
            Knoten n = nachbarn.get(i);
            if(n!=null && !n.besucht && !n.hinBesucht)
            {
                dfs(n);
                n.besucht = true;
            }
        }
        zeitStempel++;
        startKnoten.zeitStempelZurück = zeitStempel;

        //Zeitstempel für den Hinweg in beide Listen eintragen.
        for(Kante n : startKnoten.kantenZuKnoten)
        {
            n.zuKnotenZurückStempel = zeitStempel;
        }
        for(Kante n : startKnoten.kantenVonKnoten)
        {
            n.vonKnotenZurückStempel = zeitStempel;
        }

    }


    //TESTAUSGABE
    public static void ausgabe()
    {
        for (int i = 0; i<alleKnoten.size(); i++)
        {
            System.out.print("\nKnoten: " + alleKnoten.get(i).bezeichnung);
            System.out.print("  TimeStampForward: " + alleKnoten.get(i).zeitStempelHin);
            System.out.print("  TimeStampBack: " + alleKnoten.get(i).zeitStempelZurück);
        }
        System.out.print("\n");

        for(int i=0; i<alleKnoten.size(); i++)
        {
            Knoten m = alleKnoten.get(i);
            System.out.print("\nKnoten: " + m.bezeichnung);
            for(Kante n : m.kantenVonKnoten)
            {
                System.out.print("  VON:  " + n.von + "\t"+ "ZU:  " + n.zu + "\n");
                System.out.print("   vonKnotenHinStempel:  " + n.vonKnotenHinStempel + "\t\n");
                System.out.print("   vonKnotenZurückStempel:  " + n.vonKnotenZurückStempel + "\t\n");
            }
            for(Kante n : m.kantenZuKnoten)
            {
                System.out.print("  VON:  " + n.von + "\t" + "ZU:  " + n.zu + "\n");
                System.out.print("   zuKnotenHinStempel:  " + n.zuKnotenHinStempel + "\t\n");
                System.out.print("   zuKnotenZurückStempel:  " + n.zuKnotenZurückStempel + "\t\n");
            }
        }
    }

    public void test()
    {
        Rectangle rect = new Rectangle (100, 40, 100, 100);
        rect.setArcHeight(50);
        rect.setArcWidth(50);
        rect.setFill(Color.VIOLET);

        root.getChildren().add(rect);

        final Duration SEC_2 = Duration.millis(2000);
        final Duration SEC_3 = Duration.millis(3000);

        PauseTransition pt = new PauseTransition(Duration.millis(1000));
        FadeTransition ft = new FadeTransition(SEC_3);
        ft.setFromValue(1.0f);
        ft.setToValue(0.3f);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        TranslateTransition tt = new TranslateTransition(SEC_2);
        tt.setFromX(-100f);
        tt.setToX(100f);
        tt.setCycleCount(2);
        tt.setAutoReverse(true);
        RotateTransition rt = new RotateTransition(SEC_3);
        rt.setByAngle(180f);
        rt.setCycleCount(4);
        rt.setAutoReverse(true);
        ScaleTransition st = new ScaleTransition(SEC_2);
        st.setByX(1.5f);
        st.setByY(1.5f);
        st.setCycleCount(2);
        st.setAutoReverse(true);

        SequentialTransition seqT = new SequentialTransition (rect, pt, ft, tt, rt, st);
        seqT.play();
    }


    int knotenID = 0;
    public void erstelleZiehKnoten()
    {
        DoubleProperty startX = new SimpleDoubleProperty(200);
        DoubleProperty startY = new SimpleDoubleProperty(200);
        String knotenBezeichnung;
        knotenBezeichnung = eingabeFeld.getText().trim();

        //Gleiche Bezeichnungen vermeiden
        if(knotenBezeichnung.equals(""))
        {
            Random r = new Random();
            char c = (char)(r.nextInt(26) + 'a');
            knotenBezeichnung = String.valueOf(c).toUpperCase();
            if(knotenBezeichnungVorhanden(knotenBezeichnung))
            {
                knotenBezeichnung = knotenBezeichnung+knotenID;
            }
        }
        else if(knotenBezeichnungVorhanden(knotenBezeichnung))
        {
            knotenBezeichnung = knotenBezeichnung+knotenID;
        }

        //Text test = new Text(knotenBezeichnung);
        Knoten zieh = new Knoten(Color.WHITESMOKE, startX, startY, knotenBezeichnung);
        zieh.id = knotenID;
        knotenID++;

        //Neuen Knoten etwas versetzt plazieren
        zieh.setCenterX(100);
        int groesse = alleKnoten.size();
        if(groesse != 0)
        {
            int entfernungVomLetzen = (int) alleKnoten.get(groesse-1).getCenterX();
            zieh.setCenterX(entfernungVomLetzen + 30);
        }

        /**Kreis und Bezeichnung sichbar machen, Bezeichnung über den Kreis packen
         *  und Mausklicks auf Bezeichnung ignorieren*/
        root.getChildren().add(zieh);
        root.getChildren().add(zieh.text); //-------? Muss separat gemacht werden :/----------------------------------
        root.getChildren().add(zieh.stempelHin);
        root.getChildren().add(zieh.stempelZurück);
        root.getChildren().add(zieh.distanz);

        eingabeFeld.setText("");
        alleKnoten.add(zieh);
        /**Knoten-Bezeichnung in die Combo-Boxen packen*/
        comboBoxVON.getItems().add(zieh.bezeichnung);
        //comboBoxVON.getItems().addAll(zieh.bezeichnung);
        comboBoxZU.getItems().addAll(zieh.bezeichnung);
        startKnoten.getItems().addAll(zieh.bezeichnung);
        löschComboBox.getItems().addAll(zieh.bezeichnung);
    }

    /**Schauen ob die gegeben Bezeichnung schon mal vergeben wurde.*/
    private boolean knotenBezeichnungVorhanden(String knotenBezeichnung)
    {
        for(Knoten n : alleKnoten)
        {
            if(knotenBezeichnung.equals(n.bezeichnung))
            {
                return true;
            }
        }
        return false;
    }

    public void knotenLöschen()
    {
        Knoten löschKnoten = null;
        Object löschComBox = löschComboBox.getSelectionModel().getSelectedItem();
        for(Knoten n : alleKnoten)
        {
            //gewählten Knoten suchen
            if(n.bezeichnung == löschComBox)
            {
                löschKnoten = n;
                System.out.print("löschKnoten-Bezeichnung:  "+löschComBox+"\t\n");
            }
        }

        int löschIndex = löschComboBox.getSelectionModel().getSelectedIndex();
        if(löschIndex != -1)
        {
            root.getChildren().remove(löschKnoten.text);
            root.getChildren().remove(löschKnoten.stempelZurück);
            root.getChildren().remove(löschKnoten.stempelHin);
            root.getChildren().remove(löschKnoten.distanz);
            root.getChildren().remove(löschKnoten);


            System.out.print("\nVORHER löschKnoten.kantenVonKnoten.size(): " + löschKnoten.kantenVonKnoten.size() +"\n");
            System.out.print("VORHER löschKnoten.kantenZuKnoten.size(): " + löschKnoten.kantenZuKnoten.size() +"\n");

            //Suche die Kante beim gewählten Knoten und lösche falls übereinstimmt in der Gesamtliste und gewählten Knoten.
            //Alle Kanten die vom gewählten Knoten weggehen löschen
            Iterator<Kante> iterAktuelleKanten = löschKnoten.kantenVonKnoten.iterator();
            while (iterAktuelleKanten.hasNext())
            {
                Kante n = iterAktuelleKanten.next();
                root.getChildren().remove(n);
                //die Kanten auch aus "alleKanten" löschen
                Iterator<Kante> iterAlleKanten = alleKanten.iterator();
                while (iterAlleKanten.hasNext())
                {
                    Kante m = iterAlleKanten.next();
                    if(n.von == m.von && n.zu == m.zu)
                    {
                        iterAlleKanten.remove();
                    }
                }
                iterAktuelleKanten.remove();
            }

            //Alle Kanten die zu gewählten Knoten zugehen löschen
            Iterator<Kante> iterAktuelleKantenZu = löschKnoten.kantenZuKnoten.iterator();
            while (iterAktuelleKantenZu.hasNext())
            {
                Kante n = iterAktuelleKantenZu.next();
                root.getChildren().remove(n);
                //die Kanten auch aus "alleKanten" löschen
                Iterator<Kante> iterAlleKanten = alleKanten.iterator();
                while (iterAlleKanten.hasNext())
                {
                    Kante m = iterAlleKanten.next();
                    if(n.von == m.von && n.zu == m.zu)
                    {
                        iterAlleKanten.remove();
                    }
                }

                iterAktuelleKantenZu.remove();
            }

            for(int i = 0; i < alleKanten.size(); i++)
            {
                System.out.print("ALLE KANTEN: VON: " + alleKanten.get(i).vonKnoten + "  ZU: "+alleKanten.get(i).zuKnoten +"\n");
            }

            System.out.print("\nNACHER löschKnoten.kantenVonKnoten.size(): " + löschKnoten.kantenVonKnoten.size() +"\n");
            System.out.print("NACHER löschKnoten.kantenZuKnoten.size(): " + löschKnoten.kantenZuKnoten.size() +"\n");

            löschKnoten.unsichtbar = true;
            updateComboBoxen();
            zeitStempel = 0;
            bfs2.setDisable(true);
            dfs.setDisable(true);
        }
    }

    //TESTAUSGABE
    public void alleKantenAusgeben()
    {
        System.out.print("\nALLE KNOTEN GRÖSSE: " + alleKnoten.size());
        System.out.print("\nALLE KANTEN GRÖSSE: " + alleKanten.size()+"\n");
        for(int i = 0; i < alleKanten.size(); i++)
        {
            System.out.print(" ALLE KANTEN: VON: " + alleKanten.get(i).vonKnoten + "  ZU: "+alleKanten.get(i).zuKnoten +"\n");
            System.out.print(" ALLE KANTEN: VON: " + alleKanten.get(i).von + "  ZU: "+alleKanten.get(i).zu +"\n");
        }

        //TESTAUSGABE: globaleMatrix anschauen
        System.out.print("  globaleMatrix:  " + "\n");
        try
        {
            for (int i = 0; i < globaleMatrix.length; i++)
            {
                for (int j = 0; j < globaleMatrix.length; j++)
                {
                    System.out.print(globaleMatrix[i][j] + "\t");
                }
                System.out.print("\n");
            }
        }catch (Exception e) {System.out.print ("Fehler bei globalerMatrix: " + e);
        }
    }

    public void kanteLöschen()
    {
        Kante löschKante = null;
        Object löschKanteComBox = löschComboBoxKanten.getSelectionModel().getSelectedItem();
        for(Kante n : alleKanten)
        {
            //gewählten Kante suchen
            Object suchKante = n.vonKnoten + " -> " + n.zuKnoten;
            if(suchKante.equals(löschKanteComBox))
            {
                löschKante = n;
            }
        }

        int löschKanteIndex = löschComboBoxKanten.getSelectionModel().getSelectedIndex();
        if(löschKanteIndex != -1)
        {
            root.getChildren().remove(löschKante);
            alleKanten.remove(löschKante);
            updateComboBoxen();
            bfs2.setDisable(true);
            dfs.setDisable(true);
        }

    }


    private void updateComboBoxen()
    {
        löschComboBox.getItems().clear();
        comboBoxVON.getItems().clear();
        comboBoxZU.getItems().clear();
        startKnoten.getItems().clear();
        löschComboBoxKanten.getItems().clear();

        for(int i = 0; i < alleKnoten.size(); i++)
        {
            if(!(alleKnoten.get(i).unsichtbar))
            {
                löschComboBox.getItems().add(alleKnoten.get(i).bezeichnung);
                comboBoxVON.getItems().add(alleKnoten.get(i).bezeichnung);
                comboBoxZU.getItems().add(alleKnoten.get(i).bezeichnung);
                startKnoten.getItems().add(alleKnoten.get(i).bezeichnung);
            }
        }

        for(int i = 0; i < alleKanten.size(); i++)
        {
            löschComboBoxKanten.getItems().addAll(alleKanten.get(i).vonKnoten + " -> " + alleKanten.get(i).zuKnoten);
        }
    }

    public void reset()
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            root.getChildren().remove(alleKnoten.get(i).text);
            root.getChildren().remove(alleKnoten.get(i).distanz);
            root.getChildren().remove(alleKnoten.get(i).stempelZurück);
            root.getChildren().remove(alleKnoten.get(i).stempelHin);
            root.getChildren().remove(alleKnoten.get(i));
        }
        for(int i = 0; i < alleKanten.size(); i++)
        {
            root.getChildren().remove(alleKanten.get(i));
        }

        alleKnoten.clear();
        alleKanten.clear();
        updateComboBoxen();
        bfs2.setDisable(true);
        dfs.setDisable(true);

        zeitStempel = 0;
        knotenID = 0;

        //TEST
        /*
        MoveTo start = new MoveTo();
        LineTo line1 = new LineTo();
        LineTo line2 = new LineTo();

        Circle c1 = new Circle(10, 100, 5);
        Circle c2 = new Circle(50, 100, 5);
        Circle c3 = new Circle(100, 100, 5);

        c1.setFill(Color.RED);
        c2.setFill(Color.RED);
        c3.setFill(Color.RED);

        start.xProperty().bind(c1.centerXProperty());
        start.yProperty().bind(c1.centerYProperty());
        bindLinePosTo(c2, line1);
        bindLinePosTo(c3, line2);

        Path path = new Path(start, line1, line2);

        root.getChildren().add(path);
        root.getChildren().add(c1);
        root.getChildren().add(c2);
        root.getChildren().add(c3);

        animate(c1, Duration.seconds(1), 100);
        animate(c2, Duration.seconds(2), 50);
        animate(c3, Duration.seconds(0.5), 150); */

    }

    public void knotenVerbinden()
    {
        int knotenNrVon = comboBoxVON.getSelectionModel().getSelectedIndex();
        int knotenNrZu = comboBoxZU.getSelectionModel().getSelectedIndex();
        System.out.print("knotenNrVon:  "+knotenNrVon+"\t");
        System.out.print("knotenNrZu:  "+knotenNrZu+"\t\n");

        Knoten knotenVon = null;
        Knoten knotenZu = null;

        Object von = comboBoxVON.getSelectionModel().getSelectedItem();
        for(Knoten n : alleKnoten)
        {
            if(n.bezeichnung == von)
            {
                knotenVon = n;
                System.out.print("VON_Knoten:  "+von+"\t");
            }
        }
        Object zu = comboBoxZU.getSelectionModel().getSelectedItem();
        for(Knoten n : alleKnoten)
        {
            if(n.bezeichnung == zu)
            {
                knotenZu = n;
                System.out.print("ZU_Knoten:  "+zu+"\t\n\n");
            }
        }

        if( (knotenNrVon != -1) && (knotenNrZu != -1) && (knotenNrVon != knotenNrZu) )
        {
            if( !verbindungVorhanden(knotenVon.id, knotenZu.id) )
            {
                /**Kante mit VON und ZU Informationen erstellen und in Liste packen*/
                //Kante kante = new Kante(knotenNrVon, knotenNrZu, knotenVonBezeichnung, knotenZuBezeichnung);
                Kante kante = new Kante(knotenVon.id, knotenZu.id, knotenVon.bezeichnung, knotenZu.bezeichnung);
                alleKanten.add(kante);

                System.out.print("VON:  "+kante.von + "\tZU:  "+kante.zu + "\t\n"); //TODO: stimmt so

                löschComboBoxKanten.getItems().addAll(kante.vonKnoten+ " -> " + kante.zuKnoten);

                knotenVon.kantenVonKnoten.add(kante);// TODO wenn MAtrix überarbeiter wird
                knotenZu.kantenZuKnoten.add(kante);//TODO

                /**Ausgewählte Knoten mit Kante fest verbinden (bind) */
                kante.startXProperty().bind(knotenVon.centerXProperty().add(knotenVon.translateXProperty()));
                kante.startYProperty().bind(knotenVon.centerYProperty().add(knotenVon.translateYProperty()));
                kante.endXProperty().bind(knotenZu.centerXProperty().add(knotenZu.translateXProperty()));
                kante.endYProperty().bind(knotenZu.centerYProperty().add(knotenZu.translateYProperty()));

                root.getChildren().add(kante);
                kante.toBack();
            }

        }

        //Mit Mausklick Pfeil auf Punkt
//        root.setOnMouseClicked(evt -> {
//            switch (evt.getButton()) {
//                case PRIMARY:
//                    // set pos of end with arrow head
//                    kante.setEndX(evt.getX());
//                    kante.setEndY(evt.getY());
//                    break;
//                case SECONDARY:
//                    // set pos of end without arrow head
//                    kante.setStartX(evt.getX());
//                    kante.setStartY(evt.getY());
//                    break;
//            }
//        });

    }

    /**Prüft ob zwischen gegeben Knotennr. eine Verbindung bereits besteht*/
    private boolean verbindungVorhanden(int von, int zu)
    {
        for(int i=0; i < alleKanten.size(); i++)
        {
            if( (alleKanten.get(i).von == von) && (alleKanten.get(i).zu == zu) )
            {
                System.out.print("Verbindung vorhanden VON:  "+alleKanten.get(i).von + "\tZU:  "+alleKanten.get(i).zu + "\t\n"); //TEST
                return true;
            }
        }
        return false;
    }


    //TEST
    private static void bindLinePosTo(Circle circle, LineTo lineTo)
    {
        lineTo.xProperty().bind(circle.centerXProperty());
        lineTo.yProperty().bind(circle.centerYProperty());
    }

    //TEST
    private static void animate(Circle circle, Duration duration, double dy)
    {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(circle.centerYProperty(), circle.getCenterY())),
                new KeyFrame(duration, new KeyValue(circle.centerYProperty(), circle.getCenterY()+dy)));
        animation.setAutoReverse(true);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

}