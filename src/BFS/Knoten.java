package BFS;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by E.E on 21.05.2017.
 */


class Knoten extends Circle
{
    ArrayList<Kante> kantenAnKnoten = new ArrayList<>();
    ArrayList<Kante> kantenVonKnoten = new ArrayList<>(); //TODO
    ArrayList<Kante> kantenZuKnoten = new ArrayList<>(); //TODO

    int id;
    boolean unsichtbar;

    int entfernung = 0;
    Text distanz = new Text();

    boolean entfernungGesetzt;
    int zeitStempelHin;
    int zeitStempelZurück;

    //Brauche es einmal als String und einmal als "Text", cast toString ging irgendwo nicht :/
    String bezeichnung;
    Text text = new Text("");

    Text stempelHin = new Text("hin");
    Text stempelZurück = new Text("zurück");

    boolean besucht;
    boolean hinBesucht;
    boolean zurückBesucht;

    boolean startKnoten;

    DoubleProperty X;
    DoubleProperty Y;

    Knoten(Color color, DoubleProperty x, DoubleProperty y, String bezeichnung)
    {
        super(x.get(), y.get(), 30);
        setFill(color.deriveColor(1, 1, 1, 0.98));
        //Ränder um den Kreis setzen und so Sachen
        setStroke(color.GRAY);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);

        this.bezeichnung = bezeichnung;
        this.text.setText(bezeichnung);
        this.kantenAnKnoten.clear();

        //x.bind(centerXProperty());
        //y.bind(centerYProperty());
        enableDrag();
        texteBinden();
    }

    /**Macht den Knoten beweglich durch Mausklick und Ziehen*/
    private void enableDrag()
    {
        final Delta dragDelta = new Delta();
            setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override public void handle(MouseEvent mouseEvent)
                {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = getCenterX() - mouseEvent.getX();
                    dragDelta.y = getCenterY() - mouseEvent.getY();
                    getScene().setCursor(Cursor.MOVE);
                }
            });
            setOnMouseReleased(new EventHandler<MouseEvent>()
            {
                @Override public void handle(MouseEvent mouseEvent)
                {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override public void handle(MouseEvent mouseEvent)
                {
                    double newX = mouseEvent.getX() + dragDelta.x;
                    //if (newX > 0 && newX < getScene().getWidth())
                    if (newX > 0 && newX < getScene().getWidth()-320)
                    {
                        setCenterX(newX);
                    }
                    double newY = mouseEvent.getY() + dragDelta.y;
                    if (newY > 0 && newY < getScene().getHeight())
                    {
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


    /**x und y Koordinaten*/
    private class Delta
    { double x, y; }

    public void texteBinden()
    {
        text.xProperty().bind(this.centerXProperty().subtract(7));
        text.yProperty().bind(this.centerYProperty());
        text.setMouseTransparent(true);

        distanz.xProperty().bind(this.centerXProperty().subtract(5));
        distanz.yProperty().bind(this.centerYProperty().add(28));
        distanz.setMouseTransparent(true);

        stempelHin.xProperty().bind(this.centerXProperty().subtract(30));
        stempelHin.yProperty().bind(this.centerYProperty().add(35));
        stempelZurück.xProperty().bind(this.centerXProperty().add(20));
        stempelZurück.yProperty().bind(this.centerYProperty().add(35));
    }
}
