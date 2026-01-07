package ui.components;

import javafx.scene.Cursor;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Edge;

public class EdgeView {
    private static final double ARROW_SIZE = 10;
    private static final double ARROW_DISTANCE = VertexNode.RADIUS + 3;
    private static final double LABEL_OFFSET_DISTANCE = 15.0;

    private final Edge edge;
    private final Line line;
    private final Text label;
    private final Polygon arrow;
    private final boolean directed;
    private final boolean showWeight;

    public EdgeView(VertexNode from, VertexNode to, Edge edge, boolean directed, boolean showWeight) {
        this.edge = edge;
        this.directed = directed;
        this.showWeight = showWeight;

        line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1.0);

        double r = VertexNode.RADIUS;
        line.startXProperty().bind(from.layoutXProperty().add(from.translateXProperty()).add(r));
        line.startYProperty().bind(from.layoutYProperty().add(from.translateYProperty()).add(r));
        line.endXProperty().bind(to.layoutXProperty().add(to.translateXProperty()).add(r));
        line.endYProperty().bind(to.layoutYProperty().add(to.translateYProperty()).add(r));

        if (showWeight) {
            label = new Text(String.valueOf(edge.weight));
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            label.setStyle("-fx-stroke: white; -fx-stroke-width: 0.5px; -fx-fill: black;");

            label.setCursor(Cursor.HAND);
            label.setOnMouseClicked(this::handleMouseClick);
            Tooltip.install(label, new Tooltip("Double-click để sửa"));
        } else {
            label = null;
        }

        arrow = new Polygon();
        arrow.setVisible(directed);

        update();

        from.layoutXProperty().addListener(o -> update());
        from.layoutYProperty().addListener(o -> update());
        from.translateXProperty().addListener(o -> update());
        from.translateYProperty().addListener(o -> update());

        to.layoutXProperty().addListener(o -> update());
        to.layoutYProperty().addListener(o -> update());
        to.translateXProperty().addListener(o -> update());
        to.translateYProperty().addListener(o -> update());

        if (showWeight) {
            line.setCursor(Cursor.HAND);
            line.setOnMouseClicked(this::handleMouseClick);
        }
    }

    private void update() {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.hypot(dx, dy);

        if (len == 0) return;

        if (label != null) {
            double mx = (x1 + x2) / 2;
            double my = (y1 + y2) / 2;

            double ux = dx / len;
            double uy = dy / len;

            double nx = -uy * LABEL_OFFSET_DISTANCE;
            double ny = ux * LABEL_OFFSET_DISTANCE;

            label.setX(mx + nx - label.getLayoutBounds().getWidth() / 2);
            label.setY(my + ny);
        }

        if (directed) {
            double ux = dx / len;
            double uy = dy / len;

            double ex = x2 - ux * ARROW_DISTANCE;
            double ey = y2 - uy * ARROW_DISTANCE;

            double leftX = ex - ux * ARROW_SIZE - uy * ARROW_SIZE / 2;
            double leftY = ey - uy * ARROW_SIZE + ux * ARROW_SIZE / 2;

            double rightX = ex - ux * ARROW_SIZE + uy * ARROW_SIZE / 2;
            double rightY = ey - uy * ARROW_SIZE - ux * ARROW_SIZE / 2;

            arrow.getPoints().setAll(ex, ey, leftX, leftY, rightX, rightY);
        }
    }

    private void handleMouseClick(MouseEvent e) {
        if (e.getClickCount() == 2 && showWeight) showEditWeightDialog();
    }

    private void showEditWeightDialog() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(edge.weight));
        dialog.setTitle("Cập nhật trọng số");
        dialog.setHeaderText("Cạnh: " + edge.from + " -> " + edge.to);
        dialog.setContentText("Nhập trọng số mới:");
        dialog.showAndWait().ifPresent(s -> {
            try {
                int w = Integer.parseInt(s.trim());
                edge.weight = w;
                if(label!=null) { label.setText(String.valueOf(w)); update(); label.setStyle("-fx-fill: blue;"); }
            } catch(Exception ex){}
        });
    }

    public void setHighlight(Color color) {
        line.setStroke(color);
        line.setStrokeWidth(4.0);
        arrow.setFill(color);
        line.toFront();
        arrow.toFront();
        if (label != null) {
            label.setFill(color);
            label.toFront();
        }
    }

    public void highlightExplore() { setHighlight(Color.CORNFLOWERBLUE); }
    public void highlightSuccess() { setHighlight(Color.RED); }

    public void resetStyle() {
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1.0);
        arrow.setFill(Color.BLACK);
        if (label != null) {
            label.setFill(Color.BLACK);
            label.setStyle("-fx-stroke: white; -fx-stroke-width: 0.5px; -fx-fill: black;");
        }
    }

    public Line getLine() { return line; }
    public Polygon getArrow() { return arrow; }
    public Text getLabel() { return label; }
}