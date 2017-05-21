package BFS;

import javafx.scene.shape.Circle;

/**
 * Created by E.E on 21.05.2017.
 */
class Knoten_ALT
{
    int entfernung = 0;
    Circle kreis;
    boolean entfernungGesetzt;

    int data;
    boolean besucht;

    Knoten_ALT(int data, Circle kreis)
    {
        this.data = data;
        this.kreis = kreis;
    }
}
