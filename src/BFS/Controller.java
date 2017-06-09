package BFS;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
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

    public Button go;
    public Button bind;
    public Button bfs;
    public Button bfs2;
    public Button löschButton;
    public Button löschButtonKante;
    public Button reset;
    public Button dfs;

    public Button verbinden;
    public Button newKnoten;

    public Line kanteA;
    public Line kanteB;

    public Circle knotenA;
    public Circle knotenB;
    public Circle knotenC;

    public Label label;


    private Queue<Knoten_ALT> queue;
    private ArrayList<Knoten_ALT> nodes = new ArrayList<>();

    /**Warteschlange für die Knoten*/
    private Queue<Knoten> warteschlange;
    /**Listen für alle angelegten Knoten und Kanten*/
    static ArrayList<Knoten> alleKnoten = new ArrayList<>();
    static ArrayList<Knoten> knotenZumFärben = new ArrayList<>();
    static ArrayList<Kante> alleKanten = new ArrayList<>();

    public Controller()
    {
        queue = new LinkedList<>(); //ALT
        warteschlange = new LinkedList<>();
    }

    /**Limitiert die Texteingabe bei Knoten-Bezeichnung*/
    public void addTextLimiter()
    {
        final int maxLength = 4;
        eingabeFeld.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue)
            {
                if (eingabeFeld.getText().length() > maxLength) {
                    String s = eingabeFeld.getText().substring(0, maxLength);
                    eingabeFeld.setText(s);
                }
            }
        });
    }


    //ALT TEST
    public void handleButtonAction(ActionEvent e)
    {
        TranslateTransition circle1Animation = new TranslateTransition(Duration.seconds(1), knotenA);
        circle1Animation.setByY(150);

        TranslateTransition circle2Animation = new TranslateTransition(Duration.seconds(1), knotenB);
        circle2Animation.setByX(150);

        ParallelTransition animation = new ParallelTransition(circle1Animation, circle2Animation);

        animation.setAutoReverse(true);
        animation.setCycleCount(2);
        go.disableProperty().bind(animation.statusProperty().isEqualTo(Animation.Status.RUNNING));
        go.setOnAction(a -> animation.play());


        if(e.getSource()== go)
        {
            //binden();
        }
    }

    //ALT: Test-Binden
    public void binden()
    {
        kanteA.setStrokeWidth(5);
        kanteA.setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
        kanteA.setStrokeLineCap(StrokeLineCap.BUTT);
        kanteA.getStrokeDashArray().setAll(10.0, 5.0);

        label.layoutXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty()));
        label.layoutYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));

        kanteA.startXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty().subtract(-30)));
        kanteA.startYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));
        kanteA.endXProperty().bind(knotenB.layoutXProperty().add(knotenB.translateXProperty()));
        kanteA.endYProperty().bind(knotenB.layoutYProperty().add(knotenB.translateYProperty()));

        // A mit C verbinden
        kanteB.startXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty()));
        kanteB.startYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));
        kanteB.endXProperty().bind(knotenC.layoutXProperty().add(knotenC.translateXProperty()));
        kanteB.endYProperty().bind(knotenC.layoutYProperty().add(knotenC.translateYProperty()));

    }


    //ALT--------------------------------------------
    // Nachbarn mit Hilfe der Matrix finden
    // wenn adjacency_matrix[i][j]==1, dann Knoten i und j verbunden
    public ArrayList<Knoten_ALT> findNeighbours(int adjacency_matrix[][], Knoten_ALT x)
    {
        int nodeIndex = -1;

        ArrayList<Knoten_ALT> nachbar = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++)
        {
            if(nodes.get(i).equals(x))
            {
                nodeIndex = i;
                break;
            }
        }
        if(nodeIndex!=-1)
        {
            for (int j = 0; j < adjacency_matrix[nodeIndex].length; j++)
            {
                if(adjacency_matrix[nodeIndex][j]==1)
                {
                    nachbar.add(nodes.get(j));
                }
            }
        }
        return nachbar;
    }
    //ALT--------------------------------------------------------------
    //Breitensuche
    public void bfs_ALT(int adjacency_matrix[][], Knoten_ALT node)
    {
        float rot = 0.0f;
        float gruen = 0.0f;
        float blau = 0.0f;

        Color farbe = Color.color(rot, gruen, blau);

        queue.add(node);
        node.besucht = true;
        while (!queue.isEmpty())
        {
            Knoten_ALT element = queue.remove();
            System.out.print(element.data + "\t");

            rot = new Random().nextFloat();
            gruen = new Random().nextFloat();
            blau = new Random().nextFloat();
            farbe = Color.color(rot, gruen, blau);

            ArrayList<Knoten_ALT> neighbours = findNeighbours(adjacency_matrix, element);
            for (int i = 0; i < neighbours.size(); i++)
            {
                Knoten_ALT n = neighbours.get(i);

                if(!neighbours.get(i).entfernungGesetzt && !n.besucht)
                {
                    neighbours.get(i).entfernung = element.entfernung + 1;
                    neighbours.get(i).entfernungGesetzt = true;
                }

                System.out.print("NACHBAR " + neighbours.get(i).data + "\t");
                System.out.print("ENTFERNUNG " + neighbours.get(i).entfernung + "\t");


                if (n.entfernung == 1)
                {
                  n.kreis.setFill(Color.GOLD);
                }
                if (n.entfernung == 2)
                {
                    n.kreis.setFill(Color.AQUA);
                }
                if (n.entfernung == 3)
                {
                    n.kreis.setFill(Color.CHOCOLATE);
                }
                if (n.entfernung == 4)
                {
                    n.kreis.setFill(Color.AZURE);
                }

                if(n != null && !n.besucht)
                {
                    queue.add(n);
                    n.besucht = true;
                    //neighbours.get(i).kreis.setFill(Color.GOLD);
                }
            }
            System.out.print("\n");
        }
    }


    /**Start-Knoten wird ausgewählt und die Suchen aktivert*/
    public void start()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        if(start != -1)
        {
            bfs2.setDisable(false);
            dfs.setDisable(false);
        }
    }

    public void bfsAusführen()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        Knoten startKnoten = alleKnoten.get(start); //Index 8 = Knoten 9
        bfs(startKnoten);
    }

    public void dfsAusführen()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        Knoten startKnoten = alleKnoten.get(start); //Index 8 = Knoten 9
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            alleKnoten.get(i).besucht = false;
        }
        dfs(startKnoten);
        ausgabe(); //TEST-Ausgabe in Shell
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
            matrix[alleKanten.get(i).von][alleKanten.get(i).zu] = 1;
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

                knotenZumFärben.add(n);

                /**Knoten in die Warteschlange packen und als besucht markieren*/
                if(n != null && !n.besucht)
                {
                    warteschlange.add(n);
                    n.besucht = true;
                }
            }
            System.out.print("\n");
        }
        //Knoten nach Entfernung einfärben
        einfärben();
    }


    public void einfärben()
    {
        StrokeTransition übergang = new StrokeTransition(); //Linie
        DropShadow leuchten = new DropShadow();
        leuchten.setWidth(55);
        leuchten.setHeight(15);

        for (int i = 0; i < alleKnoten.size(); i++)
        {
            Knoten n = alleKnoten.get(i);
            String name = alleKnoten.get(i).bezeichnung;
            int entfernung = alleKnoten.get(i).entfernung;

            //TESTAUSGABE
            System.out.print("BEZEICHNUNG:\t"+name+"   ENTFERNUNG\t"+entfernung+"\n");

            if (n.entfernung == 0)
            {
                //n.setFill(Color.WHITE.deriveColor(1,1,1, 0.99));
            }
            if (n.entfernung == 1)
            {
                FillTransition flächeÜbergang = new FillTransition(); //Fläche

                flächeÜbergang.setShape(n);
                flächeÜbergang.setDuration(new Duration(1200));
                flächeÜbergang.setToValue(Color.GOLD.deriveColor(1,1,1, 1));
                //flächeÜbergang.setCycleCount(Timeline.INDEFINITE);
                flächeÜbergang.setCycleCount(3);
                flächeÜbergang.setAutoReverse(true);
                flächeÜbergang.play();
            }
            if (n.entfernung == 2)
            {
                PauseTransition pause = new PauseTransition(Duration.millis(3600));
                FillTransition flächeÜbergang = new FillTransition(); //Fläche

                flächeÜbergang.setShape(n);
                flächeÜbergang.setDuration(new Duration(1200));
                flächeÜbergang.setToValue(Color.AQUA.deriveColor(1,1,1, 1));
                flächeÜbergang.setCycleCount(3);
                flächeÜbergang.setAutoReverse(true);

                SequentialTransition sequence = new SequentialTransition (n, pause, flächeÜbergang);
                sequence.play();
            }
            if (n.entfernung == 3)
            {
                PauseTransition pause = new PauseTransition(Duration.millis(7200));
                FillTransition flächeÜbergang = new FillTransition(); //Fläche

                flächeÜbergang.setShape(n);
                flächeÜbergang.setDuration(new Duration(1200));
                flächeÜbergang.setToValue(Color.CHOCOLATE.deriveColor(1,1,1, 1));
                flächeÜbergang.setCycleCount(3);
                flächeÜbergang.setAutoReverse(true);

                SequentialTransition sequence = new SequentialTransition (n, pause, flächeÜbergang);
                sequence.play();
            }
            if (n.entfernung == 4)
            {
                //n.setFill(Color.AZURE);
                PauseTransition pause = new PauseTransition(Duration.millis(10800));
                FillTransition flächeÜbergang = new FillTransition(); //Fläche

                flächeÜbergang.setShape(n);
                flächeÜbergang.setDuration(new Duration(1200));
                flächeÜbergang.setToValue(Color.AZURE.deriveColor(1,1,1, 1));
                flächeÜbergang.setCycleCount(3);
                flächeÜbergang.setAutoReverse(true);

                SequentialTransition sequence = new SequentialTransition (n, pause, flächeÜbergang);
                sequence.play();
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
            matrix[alleKanten.get(i).von][alleKanten.get(i).zu] = 1;
        }

        zeitStempel++;
        startKnoten.zeitStempelHin = zeitStempel;
        startKnoten.stempelHin.setText(Integer.toString(zeitStempel));
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
        startKnoten.stempelZurück.setText(Integer.toString(zeitStempel));
    }


    public static void ausgabe()
    {
        for (int i = 0; i<alleKnoten.size(); i++)
        {
            System.out.print("\nKnoten: " + alleKnoten.get(i).bezeichnung);
            System.out.print("  TimeStampForward: " + alleKnoten.get(i).zeitStempelHin);
            System.out.print("  TimeStampBack: " + alleKnoten.get(i).zeitStempelZurück);
        }
        System.out.print("\n");
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

    public void erstelleZiehKnoten()
    {
        //test();

        DoubleProperty startX = new SimpleDoubleProperty(200);
        DoubleProperty startY = new SimpleDoubleProperty(200);
        String knotenBezeichnung;
        knotenBezeichnung = eingabeFeld.getText().trim();
        if(knotenBezeichnung.equals(""))
        {
            Random r = new Random();
            char c = (char)(r.nextInt(26) + 'a');
            knotenBezeichnung = String.valueOf(c).toUpperCase();
        }

        //Text test = new Text(knotenBezeichnung);
        Knoten zieh = new Knoten(Color.PALEGREEN, startX, startY, knotenBezeichnung);

        //Neuen Knoten etwas versetzt plazieren
        zieh.setCenterX(100);
        int groesse = alleKnoten.size();
        if(groesse != 0)
        {
            int entfernungVomLetzen = (int) alleKnoten.get(groesse-1).getCenterX();
            zieh.setCenterX(entfernungVomLetzen + 20);
        }


        /**Kreis und Bezeichnung sichbar machen, Bezeichnung über den Kreis packen
         *  und Mausklicks auf Bezeichnung ignorieren*/
        root.getChildren().add(zieh.text); //-------? Muss separat gemacht werden :/----------------------------------
        root.getChildren().add(zieh);
        root.getChildren().add(zieh.stempelHin);
        root.getChildren().add(zieh.stempelZurück);
        zieh.text.toFront();
        zieh.text.setMouseTransparent(true);

        eingabeFeld.setText("");
        alleKnoten.add(zieh);
        /**Knoten-Bezeichnung in die Combo-Boxen packen*/
        comboBoxVON.getItems().addAll(zieh.bezeichnung);
        comboBoxZU.getItems().addAll(zieh.bezeichnung);
        startKnoten.getItems().addAll(zieh.bezeichnung);
        löschComboBox.getItems().addAll(zieh.bezeichnung);

        //TEST
        //Circle c2 = new Circle(50, 100, 5);
        //Line l1 = new Line(100, 100, 200, 300);
        //root.getChildren().add(l1);

        //c2.setCenterX(zieh.getCenterX());
        //c2.centerXProperty().bind(zieh.centerXProperty().add(l1.scaleXProperty()));
        //c2.centerYProperty().bind(zieh.centerYProperty().add(l1.translateYProperty()));

        //l1.endXProperty().bind(c2.centerXProperty());
        //l1.endYProperty().bind(c2.centerYProperty());
        //c2.centerXProperty().bind(l1.endXProperty().add(50));
        //root.getChildren().add(c2);
    }

    public void knotenLöschen()
    {
        int löschIndex = löschComboBox.getSelectionModel().getSelectedIndex();
        if(löschIndex != -1)
        {
            root.getChildren().remove(alleKnoten.get(löschIndex).text);
            root.getChildren().remove(alleKnoten.get(löschIndex).stempelZurück);
            root.getChildren().remove(alleKnoten.get(löschIndex).stempelHin);
            root.getChildren().remove(alleKnoten.get(löschIndex));
            alleKnoten.remove(löschIndex);
            updateComboBoxen();
        }
        //TODO: Wenn Knoten löschen, dann alle Kanten löschen die dran hängen!
    }

    public void kanteLöschen()
    {
        int löschKanteIndex = löschComboBoxKanten.getSelectionModel().getSelectedIndex();
        if(löschKanteIndex != -1)
        {
            root.getChildren().remove(alleKanten.get(löschKanteIndex));
            //root.getChildren().remove(allePfeile.get(löschKanteIndex));
            //allePfeile.remove(löschKanteIndex);
            alleKanten.remove(löschKanteIndex);
            updateComboBoxen();
        }
    }

    private void updateAlleKnoten() //Nicht nötig, weil ArrayListen sich selbst updaten?
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            System.out.print("ALLE: INDEX:  " + alleKnoten.indexOf(i) + " INHALT  " + alleKnoten.get(i).bezeichnung + "\t\n");
        }
        ArrayList<Knoten> temp = new ArrayList<>(alleKnoten);
        for(int i = 0; i < temp.size(); i++)
        {
            System.out.print("TEMP: INDEX:  " + temp.get(i) + " INHALT  " + temp.get(i).bezeichnung + "\t\n");
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
            löschComboBox.getItems().add(alleKnoten.get(i).bezeichnung);
            comboBoxVON.getItems().add(alleKnoten.get(i).bezeichnung);
            comboBoxZU.getItems().add(alleKnoten.get(i).bezeichnung);
            startKnoten.getItems().add(alleKnoten.get(i).bezeichnung);
        }

        for(int i = 0; i < alleKanten.size(); i++)
        {
            löschComboBoxKanten.getItems().add(alleKanten.get(i).vonKnoten +"->"+ alleKanten.get(i).zuKnoten);
        }
    }


    public void reset()
    {
        for(int i = 0; i < alleKnoten.size(); i++)
        {
            root.getChildren().remove(alleKnoten.get(i).text);
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

        //TEST
/*        MoveTo start = new MoveTo();
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
        animate(c3, Duration.seconds(0.5), 150);*/
    }

    public void knotenVerbinden()
    {
        int knotenNrVon = comboBoxVON.getSelectionModel().getSelectedIndex();
        int knotenNrZu = comboBoxZU.getSelectionModel().getSelectedIndex();
        String knotenVonBezeichnung;
        String knotenZuBezeichnung;

        if( (knotenNrVon != -1) && (knotenNrZu != -1) && (knotenNrVon != knotenNrZu) )
        {
            knotenVonBezeichnung = alleKnoten.get(knotenNrVon).bezeichnung;
            knotenZuBezeichnung = alleKnoten.get(knotenNrZu).bezeichnung;

            if( !verbindungVorhanden(knotenNrVon, knotenNrZu) )
            {
                /**Kante mit VON und ZU Informationen erstellen und in Liste packen*/
                Kante kante = new Kante(knotenNrVon, knotenNrZu, knotenVonBezeichnung, knotenZuBezeichnung);
                alleKanten.add(kante);

                System.out.print("VON:  "+kante.von + "\tZU:  "+kante.zu + "\t\n");
                Knoten vonKnoten = alleKnoten.get(knotenNrVon);
                Knoten zuKnoten = alleKnoten.get(knotenNrZu);
                löschComboBoxKanten.getItems().add(kante.vonKnoten+ " -> " + kante.zuKnoten);

                /**Ausgewählte Knoten mit Kante fest verbinden (bind) */
                kante.startXProperty().bind(vonKnoten.centerXProperty().add(vonKnoten.translateXProperty()));
                kante.startYProperty().bind(vonKnoten.centerYProperty().add(vonKnoten.translateYProperty()));
                kante.endXProperty().bind(zuKnoten.centerXProperty().add(zuKnoten.translateXProperty()));
                kante.endYProperty().bind(zuKnoten.centerYProperty().add(zuKnoten.translateYProperty()));

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

    private boolean verbindungVorhanden(int von, int zu)
    {
        for(int i=0; i < alleKanten.size(); i++)
        {
            if( (alleKanten.get(i).von == von) && (alleKanten.get(i).zu == zu) )
            {
                System.out.print("bool_VON:  "+alleKanten.get(i).von + "\tbool_ZU:  "+alleKanten.get(i).zu + "\t\n"); //TEST
                return true;
            }
        }
        System.out.print("\t\n");
        return false;
    }


    //TEST
    private static void bindLinePosTo(Circle circle, LineTo lineTo)
    {
        lineTo.xProperty().bind(circle.centerXProperty());
        lineTo.yProperty().bind(circle.centerYProperty());
    }

    private static void animate(Circle circle, Duration duration, double dy)
    {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(circle.centerYProperty(), circle.getCenterY())),
                new KeyFrame(duration, new KeyValue(circle.centerYProperty(), circle.getCenterY()+dy)));
        animation.setAutoReverse(true);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    //ALT-----------------------------------------------------
    public void knotenAnlegen()
    {
        Knoten_ALT A = new Knoten_ALT(10, knotenA);
        Knoten_ALT B = new Knoten_ALT(20, knotenB);
        Knoten_ALT C = new Knoten_ALT(30, knotenC);

        nodes.add(A);
        nodes.add(B);
        nodes.add(C);

        int adjacency_matrix[][] ={
                {0,1,1}, // A: 10
                {1,0,0}, // B: 20
                {1,0,0}, // C: 30
        };

        int matrix[][] ={
                {0,0,0,0,0,0,0,0,0}, // 1
                {1,0,0,0,0,0,0,0,0}, // 2
                {1,0,0,0,0,0,0,0,0}, // 3
                {1,0,1,0,0,0,0,0,0}, // 4
                {0,1,0,0,0,0,0,0,0}, // 5
                {1,0,0,0,1,0,0,0,0}, // 6
                {0,0,0,1,1,0,0,1,0}, // 7
                {1,0,1,0,0,0,0,0,0}, // 8
                {0,0,0,0,1,1,1,0,0}, // 9
        };

        System.out.println("BFS: ");
        bfs_ALT(adjacency_matrix, A); //Matrix und Startknoten mitgeben
        bfs.setDisable(true);
    }

}