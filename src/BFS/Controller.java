package BFS;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Fuse on 06.05.2017.
 */


public class Controller
{
    @FXML
    public AnchorPane root;

    public TextField eingabeFeld;

    public ComboBox comboBox1;
    public ComboBox comboBox2;

    public Button go;
    public Button bind;
    public Button bfs;
    public Button bfs2;
    public Button verbinden;

    public Line kanteA;
    public Line kanteB;
    public Line kanteC;
    public Line kanteD;
    public Line kanteE;
    public Line kanteF;

    public Circle knotenA;
    public Circle knotenB;
    public Circle knotenC;
    public Circle knotenD;
    public Circle knotenE;
    public Circle knotenF;
    public Circle knotenG;
    public Circle knotenH;
    public Circle knotenI;



    public Label label;


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


    public void binden()
    {
        kanteA.setStrokeWidth(2);
        kanteA.setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
        kanteA.setStrokeLineCap(StrokeLineCap.BUTT);
        kanteA.getStrokeDashArray().setAll(10.0, 5.0);


        label.layoutXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty()));
        label.layoutYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));

        kanteA.startXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty()));
        kanteA.startYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));
        kanteA.endXProperty().bind(knotenB.layoutXProperty().add(knotenB.translateXProperty()));
        kanteA.endYProperty().bind(knotenB.layoutYProperty().add(knotenB.translateYProperty()));

        // A mit C verbinden
        kanteB.startXProperty().bind(knotenA.layoutXProperty().add(knotenA.translateXProperty()));
        kanteB.startYProperty().bind(knotenA.layoutYProperty().add(knotenA.translateYProperty()));
        kanteB.endXProperty().bind(knotenC.layoutXProperty().add(knotenC.translateXProperty()));
        kanteB.endYProperty().bind(knotenC.layoutYProperty().add(knotenC.translateYProperty()));

        // B mit D verbinden
        kanteD.startXProperty().bind(knotenB.layoutXProperty().add(knotenB.translateXProperty()));
        kanteD.startYProperty().bind(knotenB.layoutYProperty().add(knotenB.translateYProperty()));
        kanteD.endXProperty().bind(knotenD.layoutXProperty().add(knotenD.translateXProperty()));
        kanteD.endYProperty().bind(knotenD.layoutYProperty().add(knotenD.translateYProperty()));

        // C mit E verbinden


    }



    private Queue<Knoten> queue;
    static ArrayList<Knoten> nodes = new ArrayList<Knoten>();


    private Queue<Knoten2> warteschlange;
    static ArrayList<Knoten2> alleKnoten = new ArrayList<Knoten2>();



    public Controller()
    {
        queue = new LinkedList<Knoten>();
        warteschlange = new LinkedList<Knoten2>();

    }

    static class Knoten
    {
        int entfernung = 0;
        Circle kreis;
        boolean entfernungGesetzt;

        int data;
        boolean besucht;

        Knoten(int data, Circle kreis)
        {
            this.data = data;
            this.kreis = kreis;
        }
    }

    // Knoten erbt von Circle
    class Knoten2 extends Circle
    {
        int entfernung = 0;
        boolean entfernungGesetzt;

        //Brauche es einmal als String und einmal als "Text"
        String inhalt;
        Text text = new Text("");

        boolean besucht;

        DoubleProperty X;
        DoubleProperty Y;

        Knoten2(Color color, DoubleProperty x, DoubleProperty y, String inhalt)
        {
            //super(30, color);
            super(x.get(), y.get(), 30);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);

            this.inhalt = inhalt;
            this.text.setText(inhalt);

            //Reinfolge spielt eine Rolle
            root.getChildren().add(text);
            root.getChildren().add(Knoten2.this);


            text.layoutXProperty().bindBidirectional(centerXProperty());
            text.layoutYProperty().bindBidirectional(centerYProperty());

            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag()
        {
            final Delta dragDelta = new Delta();
            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = getCenterX() - mouseEvent.getX();
                    dragDelta.y = getCenterY() - mouseEvent.getY();
                    getScene().setCursor(Cursor.MOVE);
                }
            });
            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    double newX = mouseEvent.getX() + dragDelta.x;
                    if (newX > 0 && newX < getScene().getWidth()) {
                        setCenterX(newX);
                    }
                    double newY = mouseEvent.getY() + dragDelta.y;
                    if (newY > 0 && newY < getScene().getHeight()) {
                        setCenterY(newY);
                    }
                }
            });
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.HAND);
                    }
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });
        }

        // records relative x and y co-ordinates.
        private class Delta
        { double x, y; }
    }



    // Nachbarn mit Hilfe der Matrix finden
    // wenn adjacency_matrix[i][j]==1, dann Knoten i und j verbunden
    public ArrayList<Knoten> findNeighbours(int adjacency_matrix[][], Knoten x)
    {
        int nodeIndex = -1;

        ArrayList<Knoten> nachbar = new ArrayList<Knoten>();
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


    //Breitensuche
    public void bfs(int adjacency_matrix[][], Knoten node)
    {
        float rot = 0.0f;
        float gruen = 0.0f;
        float blau = 0.0f;

        Color farbe = Color.color(rot, gruen, blau);


        queue.add(node);
        node.besucht = true;
        while (!queue.isEmpty())
        {
            Knoten element = queue.remove();
            System.out.print(element.data + "\t");

            rot = new Random().nextFloat();
            gruen = new Random().nextFloat();
            blau = new Random().nextFloat();
            farbe = Color.color(rot, gruen, blau);


            ArrayList<Knoten> neighbours = findNeighbours(adjacency_matrix, element);
            for (int i = 0; i < neighbours.size(); i++)
            {
                Knoten n = neighbours.get(i);

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



    public void test()
    {
        //DoubleProperty startX = new SimpleDoubleProperty(100);
        //DoubleProperty startY = new SimpleDoubleProperty(100);
        //String knotenBezeichnung;
        //knotenBezeichnung = eingabeFeld.getText();

        //Knoten2 zieh = new Knoten2(Color.PALEGREEN, startX, startY, knotenBezeichnung);
        //alleKnoten.add(zieh);

        //Startknotten aus dem Array nehmen und los gehst
        Knoten2 startKnotten = alleKnoten.get(8); //Index 8 = Knoten 9

        bfs2(startKnotten);
    }


    public ArrayList<Knoten2> findNeighbours2(int[][] adjacency_matrix, Knoten2 x)
    {
        int nodeIndex = -1;

        ArrayList<Knoten2> nachbar = new ArrayList<Knoten2>();
        for (int i = 0; i < alleKnoten.size(); i++)
        {
            if(alleKnoten.get(i).equals(x))
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
                    nachbar.add(alleKnoten.get(j));
                }
            }
        }
        return nachbar;
    }

    //Breitensuche
    public void bfs2(Knoten2 node)
    {
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


        warteschlange.add(node);
        node.besucht = true;

        while (!warteschlange.isEmpty())
        {
            Knoten2 element = warteschlange.remove();
            System.out.print(element.inhalt + "\t"); //macht der


            ArrayList<Knoten2> neighbours = findNeighbours2(matrix, element);
            for (int i = 0; i < neighbours.size(); i++)
            {
                Knoten2 n = neighbours.get(i);

                if(!neighbours.get(i).entfernungGesetzt && !n.besucht)
                {
                    neighbours.get(i).entfernung = element.entfernung + 1;
                    neighbours.get(i).entfernungGesetzt = true;
                }

                System.out.print("NACHBAR " + neighbours.get(i).inhalt + "\t");
                System.out.print("ENTFERNUNG " + neighbours.get(i).entfernung + "\t");


                if (n.entfernung == 0)
                {
                    n.setFill(Color.CHOCOLATE.deriveColor(1,1,1, 0.5));
                }
                if (n.entfernung == 1)
                {
                    n.setFill(Color.GOLD.deriveColor(1,1,1, 0.5));
                }
                if (n.entfernung == 2)
                {
                    n.setFill(Color.AQUA.deriveColor(1,1,1, 0.5));
                }
                if (n.entfernung == 3)
                {
                    n.setFill(Color.CHOCOLATE.deriveColor(1,1,1, 0.5));
                }
                if (n.entfernung == 4)
                {
                    n.setFill(Color.AZURE);
                }

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


    public void erstellKnoten()
    {
        Circle kreis = new Circle(20, Color.GREEN);
        root.getChildren().add(kreis);

        // Kreis verschieben
        kreis.setTranslateY(300);
        kreis.setTranslateX(300);

        Knoten knoten = new Knoten(10, kreis);
    }


    public void erstelleZiehKnoten()
    {
        DoubleProperty startX = new SimpleDoubleProperty(100);
        DoubleProperty startY = new SimpleDoubleProperty(100);
        String knotenBezeichnung;
        knotenBezeichnung = eingabeFeld.getText();

        Knoten2 zieh = new Knoten2(Color.PALEGREEN, startX, startY, knotenBezeichnung);

        eingabeFeld.setText("");

        alleKnoten.add(zieh);

        comboBox1.getItems().addAll(zieh.inhalt);
        comboBox2.getItems().addAll(zieh.inhalt);

        //Label label = new Label("HiiiiiiiiiiiiiiiiIIIIIIII");
        //Text text = new Text("TEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEST");

//        text.layoutXProperty().bind(zieh.layoutXProperty().add(zieh.translateXProperty()));
//        text.layoutYProperty().bind(zieh.layoutYProperty().add(zieh.translateYProperty()));

        //text.setTranslateX(300);
        //text.setTranslateY(300);
        //text.layoutXProperty().bind(zieh.layoutXProperty());
        //text.layoutYProperty().bind(zieh.layoutYProperty());

        //label.layoutYProperty().bind(startY);
        //label.layoutXProperty().bind(startX);

        //root.getChildren().add(label);

        //label.layoutXProperty().bind(zieh.centerXProperty());
        //label.layoutYProperty().bind(zieh.centerYProperty());

        //root.getChildren().add(zieh);
        //root.getChildren().addAll(zieh, label);

        //zieh.radiusProperty().bind(label.widthProperty());
        //zieh.setFill(Color.PALEGREEN);

    }

    public void knotenVerbinden()
    {
        int knotenNr = comboBox1.getSelectionModel().getSelectedIndex();
        int knotenNr2 = comboBox2.getSelectionModel().getSelectedIndex();

        System.out.print(knotenNr + "\t");
        System.out.print(alleKnoten.get(knotenNr).inhalt + "\t"+"\n");

        Line kante = new Line();

        Knoten2 test = alleKnoten.get(knotenNr);
        Knoten2 test2 = alleKnoten.get(knotenNr2);


        //System.out.print("X:  " + test.centerXProperty().toString() + "\t");
        //System.out.print("Y:  " + test.centerYProperty().toString() + "\t" + "\n");

        //kante.startXProperty().set(test.centerXProperty().doubleValue());
        //kante.startYProperty().set(test.centerXProperty().doubleValue());

        kante.startXProperty().bind(test.centerXProperty().add(test.translateXProperty()));
        kante.startYProperty().bind(test.centerYProperty().add(test.translateYProperty()));
        kante.endXProperty().bind(test2.centerXProperty().add(test2.translateXProperty()));
        kante.endYProperty().bind(test2.centerYProperty().add(test2.translateYProperty()));

        root.getChildren().add(kante);
    }




    public void knotenAnlegen()
    {
        Knoten A = new Knoten(10, knotenA);
        Knoten B = new Knoten(20, knotenB);
        Knoten C = new Knoten(30, knotenC);
        Knoten D = new Knoten(40, knotenD);
        Knoten E = new Knoten(50, knotenE);
        Knoten F = new Knoten(60, knotenF);
        Knoten G = new Knoten(70, knotenG);
        Knoten H = new Knoten(80, knotenH);
        Knoten I = new Knoten(90, knotenI);

        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        nodes.add(E);
        nodes.add(F);
        nodes.add(G);
        nodes.add(H);
        nodes.add(I);


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
        bfs(matrix, I); //Matrix und Startknoten mitgeben
        bfs.setDisable(true);
    }

}


