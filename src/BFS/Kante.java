package BFS;

import javafx.scene.shape.Line;

/**
 * Created by E.E on 21.05.2017.
 */
class Kante extends Line
{
    int von;
    int zu;
    String vonKnoten;
    String zuKnoten;

    Kante(int von, int zu, String vonKnoten, String zuKnoten)
    {
        this.von = von;
        this.zu = zu;
        this.vonKnoten = vonKnoten;
        this.zuKnoten = zuKnoten;
    }
}
