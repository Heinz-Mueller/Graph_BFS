package BFS;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Fuse on 06.05.2017.
 */


public class Controller
{
    @FXML
    public AnchorPane root;

    public Button go;
    public Button bind;
    public Button bfs;

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

        // A mit B verbinden
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

    private Queue<Knoten> warteschlange;
    static ArrayList<Circle> alleKnoten = new ArrayList<Circle>();



    public Controller()
    {
        queue = new LinkedList<Knoten>();
        warteschlange = new LinkedList<Knoten>();

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

    static class graphKnoten
    {
        int data;
        boolean besucht;

        graphKnoten(int data)
        {
            this.data = data;
        }
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


    public void bfs(int adjacency_matrix[][], Knoten node)
    {
        int rot = 100;
        int gruen = 255;
        int blau = 255;

        Color farbe = Color.rgb(100, 150, 255);

        queue.add(node);
        node.besucht = true;
        while (!queue.isEmpty())
        {
            Knoten element = queue.remove();
            System.out.print(element.data + "\t");

            farbe = Color.rgb(rot, gruen, blau);

            //element.kreis.setFill(farbe);

            rot = rot - 20;
            gruen = gruen - 0;
            blau = blau - 60;

            ArrayList<Knoten> neighbours = findNeighbours(adjacency_matrix, element);
            for (int i = 0; i < neighbours.size(); i++)
            {
                Knoten n = neighbours.get(i);
                //element.kreis.setFill(Color.GOLD);
                if(!neighbours.get(i).entfernungGesetzt && !n.besucht)
                {
                    neighbours.get(i).entfernung = element.entfernung + 1;
                    neighbours.get(i).entfernungGesetzt = true;
                }
                //neighbours.get(i).entfernung = element.entfernung + 1;

                System.out.print("NACHBAR " + neighbours.get(i).data + "\t");
                System.out.print("ENTFERNUNG " + neighbours.get(i).entfernung + "\t");



//                n.entfernung = n.entfernung + 1;

//                System.out.print(" ENTFERNUNG: " + n.entfernung + " ");
                if (n.entfernung == 1)
                {
                  n.kreis.setFill(Color.GREEN);
                }
                if (n.entfernung == 2)
                {
                    n.kreis.setFill(Color.GOLD);
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


    public void knotenAnlegen()
    {
        Knoten A = new Knoten(10, knotenA);
        Knoten B = new Knoten(20, knotenB);
        Knoten C = new Knoten(30, knotenC);
        Knoten D = new Knoten(40, knotenD);
        Knoten E = new Knoten(50, knotenE);

        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        nodes.add(E);

        alleKnoten.add(knotenA);
        alleKnoten.add(knotenB);
        alleKnoten.add(knotenC);
        alleKnoten.add(knotenD);
        alleKnoten.add(knotenE);

        int adjacency_matrix[][] ={
                {0,1,1,0,0}, // A: 40
                {1,0,0,1,0}, // B :10
                {1,0,0,0,1}, // C: 20
                {0,1,0,0,0}, // D: 30
                {0,0,1,0,0}, // E: 60
        };

        System.out.println("BFS: ");
        bfs(adjacency_matrix, A);
        bfs.setDisable(true);
    }

}


