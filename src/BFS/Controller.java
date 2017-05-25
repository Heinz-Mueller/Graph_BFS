package BFS;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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

    public Button go;
    public Button bind;
    public Button bfs;
    public Button bfs2;

    public Button verbinden;
    public Button newKnoten;

    public Line kanteA;
    public Line kanteB;

    public Circle knotenA;
    public Circle knotenB;
    public Circle knotenC;
    public Circle knotenD;
    public Circle knotenE;

    public Label label;


    private Queue<Knoten_ALT> queue;
    private ArrayList<Knoten_ALT> nodes = new ArrayList<>();

    /**Warteschlange für die Knoten*/
    private Queue<Knoten> warteschlange;
    /**Listen für alle angelegten Knoten und Kanten*/
    static ArrayList<Knoten> alleKnoten = new ArrayList<>();
    static ArrayList<Kante> alleKanten = new ArrayList<>();


    public Controller()
    {
        queue = new LinkedList<>(); //ALT
        warteschlange = new LinkedList<>();
    }

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


    /**Start-Knoten wird ausgewählt und BFS mit Start-Knoten ausführen*/
    public void start()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        if(start != -1)
        {
            bfs2.setDisable(false);
        }
    }

    public void bfsAusführen()
    {
        int start = startKnoten.getSelectionModel().getSelectedIndex();
        Knoten startKnoten = alleKnoten.get(start); //Index 8 = Knoten 9
        bfs(startKnoten);
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

    /**Breitensuche mit startKnoten*/
    public void bfs(Knoten startKnoten)
    {
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

            ArrayList<Knoten> neighbours = findeNachbar(matrix, element);
            for (int i = 0; i < neighbours.size(); i++)
            {
                Knoten n = neighbours.get(i);

                /**Wenn Knoten noch nicht besucht wurde und seine Entfernung noch nicht eingetragen ist
                 * Entfernung vom Nachbarn nehmen und einen Schritt dazu addieren*/
                if(!neighbours.get(i).entfernungGesetzt && !n.besucht)
                {
                    neighbours.get(i).entfernung = element.entfernung + 1;
                    neighbours.get(i).entfernungGesetzt = true;
                }

                System.out.print("NACHBAR " + neighbours.get(i).inhalt + "\t");
                System.out.print("ENTFERNUNG " + neighbours.get(i).entfernung + "\t");

                /**Knoten nach Entfernung einfärben*/
                //TODO eine Funktion die Entfernung nimmt und Farbe zurückgibt
                if (n.entfernung == 0)
                {
                    n.setFill(Color.WHITE.deriveColor(1,1,1, 0.99));
                }
                if (n.entfernung == 1)
                {
                    n.setFill(Color.GOLD.deriveColor(1,1,1, 0.99));
                }
                if (n.entfernung == 2)
                {
                    n.setFill(Color.AQUA.deriveColor(1,1,1, 0.99));
                }
                if (n.entfernung == 3)
                {
                    n.setFill(Color.CHOCOLATE.deriveColor(1,1,1, 0.99));
                }
                if (n.entfernung == 4)
                {
                    n.setFill(Color.AZURE);
                }
                /**Knoten in die Warteschlange packen und als besucht markieren*/
                if(n != null && !n.besucht)
                {
                    warteschlange.add(n);
                    n.besucht = true;
                    //neighbours.get(i).setFill(Color.GOLD);
                }
            }
            System.out.print("\n");
        }
    }


    public void erstelleZiehKnoten()
    {
        DoubleProperty startX = new SimpleDoubleProperty(200);
        DoubleProperty startY = new SimpleDoubleProperty(200);
        String knotenBezeichnung;
        knotenBezeichnung = eingabeFeld.getText().trim();
        if(knotenBezeichnung.equals(""))
        {
            knotenBezeichnung = "x";
            //TODO wenn keine Bezeichnung eingegeben wurde, dann "bla bla..."
        }

        Knoten zieh = new Knoten(Color.PALEGREEN, startX, startY, knotenBezeichnung);

        //In TESTPHASE
        zieh.setCenterX(100);
        int groesse = alleKnoten.size();
        if(groesse != 0)
        {
            int entfernungVomLetzen = (int) alleKnoten.get(groesse-1).getCenterX();
            zieh.setCenterX(entfernungVomLetzen + 10);
        }


        /**Kreis und Bezeichnung sichbar machen, Bezeichnung über den Kreis packen
         *  und Mausklicks auf Bezeichnung ignorieren*/
        root.getChildren().add(zieh.text);
        root.getChildren().add(zieh);
        zieh.text.toFront();
        zieh.text.setMouseTransparent(true);

        eingabeFeld.setText("");
        alleKnoten.add(zieh);
        /**Knoten-Bezeichnung in die Combo-Boxen packen*/
        comboBoxVON.getItems().addAll(zieh.inhalt);
        comboBoxZU.getItems().addAll(zieh.inhalt);
        startKnoten.getItems().addAll(zieh.inhalt);
    }

    public void knotenVerbinden()
    {
        int knotenNrVon = comboBoxVON.getSelectionModel().getSelectedIndex();
        int knotenNrZu = comboBoxZU.getSelectionModel().getSelectedIndex();

        /**Kante mit VON und ZU Informationen erstellen und in Liste packen*/
        Kante kante = new Kante();
        kante.von = knotenNrVon;
        kante.zu = knotenNrZu;
        alleKanten.add(kante);

        System.out.print("VON:  "+kante.von + "\tZU:  "+kante.zu + "\t\n");


        /**Ausgewählte Knoten mit Kante fest verbinden (bind) */
        Knoten vonKnoten = alleKnoten.get(knotenNrVon);
        Knoten zuKnoten = alleKnoten.get(knotenNrZu);

        //System.out.print("X:  " + test.centerXProperty().toString() + "\t");
        //System.out.print("Y:  " + test.centerYProperty().toString() + "\t" + "\n");

        //kante.startXProperty().set(test.centerXProperty().doubleValue());
        //kante.startYProperty().set(test.centerXProperty().doubleValue());

        //kante.startXProperty().bind(vonKnoten.centerXProperty().add(vonKnoten.translateXProperty()).subtract(-30));
        kante.startXProperty().bind(vonKnoten.centerXProperty().add(vonKnoten.translateXProperty()));
        kante.startYProperty().bind(vonKnoten.centerYProperty().add(vonKnoten.translateYProperty()));
        kante.endXProperty().bind(zuKnoten.centerXProperty().add(zuKnoten.translateXProperty()));
        kante.endYProperty().bind(zuKnoten.centerYProperty().add(zuKnoten.translateYProperty()));

        //root.getChildren().add(kante);
        kante.toBack();



        //TEST

        Arrow b = new Arrow();
        root.getChildren().add(b);
        b.startXProperty().bind(vonKnoten.centerXProperty().add(vonKnoten.translateXProperty()));
        b.startYProperty().bind(vonKnoten.centerYProperty().add(vonKnoten.translateYProperty()));
        b.endXProperty().bind(zuKnoten.centerXProperty().add(zuKnoten.translateXProperty()));
        b.endYProperty().bind(zuKnoten.centerYProperty().add(zuKnoten.translateYProperty()));
        b.toBack();

        //Mit MAusklick Pfeil auf Punkt
//        root.setOnMouseClicked(evt -> {
//            switch (evt.getButton()) {
//                case PRIMARY:
//                    // set pos of end with arrow head
//                    b.setEndX(evt.getX());
//                    b.setEndY(evt.getY());
//                    break;
//                case SECONDARY:
//                    // set pos of end without arrow head
//                    b.setStartX(evt.getX());
//                    b.setStartY(evt.getY());
//                    break;
//            }
//        });


//        b.setStartX(150);
//        b.setStartY(300);
//        b.setEndX(500);
//        b.setEndY(350);

        //TEST-ENDE
    }


    //ALT-----------------------------------------------------
    public void knotenAnlegen()
    {
        Knoten_ALT A = new Knoten_ALT(10, knotenA);
        Knoten_ALT B = new Knoten_ALT(20, knotenB);
        Knoten_ALT C = new Knoten_ALT(30, knotenC);
        Knoten_ALT D = new Knoten_ALT(40, knotenD);
        Knoten_ALT E = new Knoten_ALT(50, knotenE);

        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        nodes.add(E);

        int adjacency_matrix[][] ={
                {0,1,1,0,0}, // A: 10
                {1,0,0,1,0}, // B: 20
                {1,0,0,0,1}, // C: 30
                {0,1,0,0,0}, // D: 40
                {0,0,1,0,0}, // E: 50
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
        bfs_ALT(adjacency_matrix, E); //Matrix und Startknoten mitgeben
        bfs.setDisable(true);
    }
}
