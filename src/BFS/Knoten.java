package BFS;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

/**
 * Created by E.E on 21.05.2017.
 */


// Knoten erbt von Circle
class Knoten extends Circle
{
    int vonMir = 0;
    int zuMir = 0;
    public int vonMirZurNR;
    public int zuMirVonNR;

    int entfernung = 0;
    boolean entfernungGesetzt;

    //Brauche es einmal als String und einmal als "Text"
    String inhalt;
    Text text = new Text("");

    boolean besucht;

    DoubleProperty X;
    DoubleProperty Y;

    Knoten(Color color, DoubleProperty x, DoubleProperty y, String inhalt)
    {
        //super(30, color);
        super(x.get(), y.get(), 30);
        setFill(color.deriveColor(1, 1, 1, 0.9));
        setStroke(color.GRAY);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);

        this.inhalt = inhalt;
        this.text.setText(inhalt);

        text.layoutXProperty().bindBidirectional(centerXProperty());
        text.layoutYProperty().bindBidirectional(centerYProperty());

        x.bind(centerXProperty());
        y.bind(centerYProperty());
        enableDrag();
    }

    //Macht den Knoten beweglich durch Mausklick und Ziehen
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
