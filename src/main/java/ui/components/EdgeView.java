package ui.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Edge;

public class EdgeView {
    private static final double LABEL_OFFSET = 15;
    private static final double ARROW_SIZE = 10;
    private static final double ARROW_DISTANCE = VertexNode.RADIUS + 3;

    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final double DEFAULT_WIDTH = 1.0;
    private static final double HIGHLIGHT_WIDTH = 3.0;

    private final Line line;
    private final Text label;
    private final Polygon arrow;
    private final boolean directed;

    public EdgeView(VertexNode from, VertexNode to, Edge edge, boolean directed, boolean showWeight) {
        this.directed = directed;

        line = new Line();
        line.setStroke(DEFAULT_COLOR);
        line.setStrokeWidth(DEFAULT_WIDTH);

        double r = VertexNode.RADIUS;

        line.startXProperty().bind(from.layoutXProperty().add(from.translateXProperty()).add(r));
        line.startYProperty().bind(from.layoutYProperty().add(from.translateYProperty()).add(r));

        line.endXProperty().bind(to.layoutXProperty().add(to.translateXProperty()).add(r));
        line.endYProperty().bind(to.layoutYProperty().add(to.translateYProperty()).add(r));

        if (showWeight) {
            label = new Text(String.valueOf(edge.weight));
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            label.setMouseTransparent(true);
        } else {
            label = null;
        }

        arrow = new Polygon();
        arrow.setFill(DEFAULT_COLOR);
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
            double nx = -dy / len;
            double ny = dx / len;

            label.setX(mx + nx * LABEL_OFFSET - label.getLayoutBounds().getWidth() / 2);
            label.setY(my + ny * LABEL_OFFSET);
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

    public void setHighlight(Color color) {
        // 1. Đổi màu
        line.setStroke(color);

        // 2. Làm đậm lên (Bình thường là 1.0, khi chọn thì tăng lên 4.0)
        line.setStrokeWidth(4.0);

        // 3. Đổi màu mũi tên (nếu có)
        arrow.setFill(color);

        // 4. Đổi màu text trọng số (nếu có)
        if (label != null) {
            label.setFill(color);
            label.setStyle("-fx-font-weight: bold;");
            label.toFront();
        }

        // 5. QUAN TRỌNG: Đưa dòng kẻ lên trên cùng để không bị các dòng khác che khuất
        line.toFront();
        arrow.toFront();
    }

    // 1. Khi thuật toán đang "nhìn" hoặc "duyệt qua" cạnh (Màu xanh dương)
    public void highlightExplore() {
        applyStyle(Color.CORNFLOWERBLUE, 2.0);
    }

    // 2. Khi cạnh được chọn vào kết quả (MST, Shortest Path) (Màu đỏ/cam đậm)
    public void highlightSuccess() {
        applyStyle(Color.RED, 4.0);
        // Đưa lên trên cùng để đè lên các cạnh khác
        line.toFront();
        arrow.toFront();
        if (label != null) label.toFront();
    }

    // 3. Reset về mặc định
    public void resetStyle() {
        applyStyle(Color.BLACK, 1.0);
        if (label != null) label.setStyle(""); // Bỏ in đậm
    }

    // Hàm phụ trợ để tránh lặp code
    private void applyStyle(Color color, double width) {
        line.setStroke(color);
        line.setStrokeWidth(width);
        arrow.setFill(color);
        if (label != null) {
            label.setFill(color);
            if (width > 2.0) label.setStyle("-fx-font-weight: bold;");
        }
    }

    public Line getLine() { return line; }
    public Text getLabel() { return label; }
    public Polygon getArrow() { return arrow; }
}