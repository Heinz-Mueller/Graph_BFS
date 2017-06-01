package BFS;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Arrow extends Group
{

    private final Line line;

    public Arrow()
    {
        //this(new Line(), new Line(), new Line());
        this(new Line(), new Line(), new Line() );
    }

    private static final double arrowLength = 50;
    private static final double arrowWidth = 3.0;


    private Arrow(Line line, Line arrow1, Line arrow2)
    {
        super(line, arrow1, arrow2);
        this.line = line;

        //Linie-Eigenschaften
        line.setSmooth(true);
        line.setStrokeWidth(1.2);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.getStrokeDashArray().addAll(20.0, 5.0);
        line.setStroke(Color.GRAY.deriveColor(1,1,1, 0.0));

        //Pfeil-Eigenschaften
        arrow1.setSmooth(true);
        arrow2.setSmooth(true);
        arrow1.setStroke(Color.GREY.deriveColor(1,1,1, 0.7));
        arrow2.setStroke(Color.GREY.deriveColor(1,1,1, 0.7));
        arrow1.getStrokeDashArray().addAll(20.0, 32.0);
        arrow2.getStrokeDashArray().addAll(20.0, 32.0);
        arrow1.setStrokeWidth(1.5);
        arrow2.setStrokeWidth(1.5);
        arrow1.setStrokeLineCap(StrokeLineCap.ROUND);
        arrow2.setStrokeLineCap(StrokeLineCap.ROUND);

        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();


            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            //binden funktioniert auch
            //arrow1.endXProperty().bind(line.endXProperty());
            //arrow1.endYProperty().bind(line.endYProperty().add(line.translateYProperty()));
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);


            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };

        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) { line.setStartX(value); }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
    }

    public final double getEndX()
    {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() { return line.endXProperty(); }

    public final void setEndY(double value) {line.setEndY(value);
    }

    public final double getEndY()
    {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

}