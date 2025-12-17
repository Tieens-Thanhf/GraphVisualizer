package ui.components;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Vertex;

public class VertexNode extends StackPane {

    public static final double RADIUS = 18;

    private final Vertex vertex;
    private final Circle circle;
    private final Label label;

    public VertexNode(Vertex v) {
        this.vertex = v;

        circle = new Circle(RADIUS);
        circle.setFill(Color.LIGHTGRAY);
        circle.setStroke(Color.BLACK);

        label = new Label(String.valueOf(v.id));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setMouseTransparent(true);

        getChildren().addAll(circle, label);

        setLayoutX(v.x - RADIUS);
        setLayoutY(v.y - RADIUS);

        setCursor(Cursor.HAND);

        enableDrag();
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void highlightVisit() {
        circle.setFill(Color.ORANGE);
    }

    public void highlightFinish() {
        circle.setFill(Color.LIGHTGREEN);
    }

    public void highlightResult() {
        // Màu đỏ rực rỡ để phân biệt với màu xanh lá (visited)
        circle.setFill(Color.RED);
        // Có thể thêm viền vàng cho nổi
        circle.setStroke(Color.GOLD);
        circle.setStrokeWidth(2);
    }

    public void resetColor() {
        circle.setFill(Color.LIGHTGRAY);
    }

    private void enableDrag() {
        final Delta dragDelta = new Delta();

        setOnMousePressed(e -> {
            dragDelta.x = getLayoutX() - e.getSceneX();
            dragDelta.y = getLayoutY() - e.getSceneY();
            toFront();
        });

        setOnMouseDragged(e -> {
            double newX = e.getSceneX() + dragDelta.x;
            double newY = e.getSceneY() + dragDelta.y;

            setLayoutX(newX);
            setLayoutY(newY);

            vertex.x = newX + RADIUS;
            vertex.y = newY + RADIUS;
        });
    }

    public void setCustomColor(Color color) {
        circle.setFill(color);
        circle.setStroke(Color.WHITE); // Viền trắng cho nổi trên nền màu đậm
        circle.setStrokeWidth(2.0);
    }

    private static class Delta { double x, y; }

    public DoubleProperty xProperty() {
        return layoutXProperty();
    }
    public DoubleProperty yProperty() {
        return layoutYProperty();
    }
}