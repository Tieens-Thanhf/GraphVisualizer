package ui;

import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import model.Edge;
import model.Graph;
import model.Vertex;
import ui.components.EdgeView;
import ui.components.VertexNode;

import java.util.HashMap;
import java.util.Map;

public class GraphPane extends Pane {
    private final Graph graph;

    private final Map<Integer, VertexNode> vertexNodes = new HashMap<>();
    private final Map<String, EdgeView> edgeViews = new HashMap<>();

    // Group chứa nội dung (Cạnh + Đỉnh) để Zoom/Pan
    private final Group contentGroup = new Group();

    private final Group edgeGroup = new Group();
    private final Group nodeGroup = new Group();

    // Các biến dùng cho Zoom/Pan
    private final Scale scaleTransform = new Scale(1, 1);
    private final Translate translateTransform = new Translate(0, 0);
    private double lastMouseX, lastMouseY;

    public GraphPane(Graph graph) {
        this.graph = graph;

        // CSS Background Grid (Lưới)
        setStyle("-fx-background-color: white; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");
        drawGrid();

        // Cấu trúc: GraphPane -> contentGroup -> [edgeGroup, nodeGroup]
        contentGroup.getChildren().addAll(edgeGroup, nodeGroup);

        // Áp dụng Transform vào contentGroup
        contentGroup.getTransforms().addAll(translateTransform, scaleTransform);

        getChildren().add(contentGroup);

        // Đăng ký sự kiện Zoom và Pan
        setupZoomAndPan();
    }

    private void setupZoomAndPan() {
        // 1. ZOOM (Lăn chuột)
        setOnScroll((ScrollEvent event) -> {
            if (event.isControlDown()) return; // Đề phòng xung đột phím tắt

            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor; // Zoom out
            }

            // Giới hạn Zoom (từ 0.2x đến 5.0x)
            double currentScale = scaleTransform.getX();
            if (currentScale * zoomFactor < 0.2 || currentScale * zoomFactor > 5.0) {
                return;
            }

            // Tính toán vị trí chuột tương đối trong contentGroup
            // Công thức này giúp Zoom ngay tại đầu con trỏ chuột (Google Maps style)
            double mouseX = (event.getX() - translateTransform.getX()) / currentScale;
            double mouseY = (event.getY() - translateTransform.getY()) / currentScale;

            // Thực hiện Scale
            scaleTransform.setX(scaleTransform.getX() * zoomFactor);
            scaleTransform.setY(scaleTransform.getY() * zoomFactor);

            // Điều chỉnh Translate để giữ vị trí chuột cố định sau khi zoom
            double newScale = scaleTransform.getX();
            translateTransform.setX(translateTransform.getX() - (mouseX * newScale - mouseX * currentScale));
            translateTransform.setY(translateTransform.getY() - (mouseY * newScale - mouseY * currentScale));

            event.consume();
        });

        // 2. PAN (Giữ chuột phải để kéo)
        setOnMousePressed(event -> {
            // Chỉ bắt sự kiện chuột phải (Chuột trái để kéo Node rồi)
            if (event.getButton() == MouseButton.SECONDARY) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
                setCursor(javafx.scene.Cursor.MOVE); // Đổi con trỏ thành mũi tên 4 chiều
            }
        });

        setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                double deltaX = event.getSceneX() - lastMouseX;
                double deltaY = event.getSceneY() - lastMouseY;

                translateTransform.setX(translateTransform.getX() + deltaX);
                translateTransform.setY(translateTransform.getY() + deltaY);

                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }

    // Hàm vẽ lưới nền (Grid) - Dùng CSS để khi Pan lưới không bị trôi đi (Tạo cảm giác vô tận)
    private void drawGrid() {
        this.setStyle(
                "-fx-background-color: white, " +
                        "linear-gradient(from 0.5px 0px to 10.5px 0px, repeat, #f0f0f0 5%, transparent 5%), " +
                        "linear-gradient(from 0px 0.5px to 0px 10.5px, repeat, #f0f0f0 5%, transparent 5%);"
        );
    }

    // === Reset góc nhìn về mặc định ===
    public void resetView() {
        scaleTransform.setX(1);
        scaleTransform.setY(1);
        translateTransform.setX(0);
        translateTransform.setY(0);
    }

    // ... (Giữ nguyên phần drawFromGraph, getEdge, getNodes, resetVisual bên dưới) ...
    public void drawFromGraph() {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        vertexNodes.clear();
        edgeViews.clear();

        for (Vertex v : graph.getVertices()) {
            VertexNode node = new VertexNode(v);
            vertexNodes.put(v.id, node);
            nodeGroup.getChildren().add(node);
        }

        for (Edge e : graph.getEdges()) {
            VertexNode uNode = vertexNodes.get(e.from);
            VertexNode vNode = vertexNodes.get(e.to);
            if (uNode == null || vNode == null) continue;

            EdgeView ev = new EdgeView(uNode, vNode, e, graph.isDirected(), graph.isWeighted());
            edgeViews.put(e.from + "-" + e.to, ev);
            if (!graph.isDirected()) edgeViews.put(e.to + "-" + e.from, ev);

            edgeGroup.getChildren().add(ev.getLine());
            if (graph.isDirected()) edgeGroup.getChildren().add(ev.getArrow());
            if (ev.getLabel() != null) edgeGroup.getChildren().add(ev.getLabel());
        }
    }

    public Map<Integer, VertexNode> getNodes() { return vertexNodes; }
    public Map<String, EdgeView> getEdgeViews() { return edgeViews; } // Helper nếu cần

    public void resetVisual() {
        vertexNodes.values().forEach(VertexNode::resetColor);
        edgeViews.values().forEach(EdgeView::resetStyle);
    }

    public void highlightPathNode(int u) {
        VertexNode n = vertexNodes.get(u);
        if (n != null) n.highlightResult();
    }

    public void highlightEdge(int u, int v, Color color) {
        EdgeView ev = getEdge(u, v);
        if (ev != null) ev.setHighlight(color);
    }

    public void applyVisit(int u) {
        VertexNode n = vertexNodes.get(u);
        if (n != null) n.highlightVisit();
    }

    public void clear() {
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();
        vertexNodes.clear();
        edgeViews.clear();
    }

    public EdgeView getEdge(int u, int v) {
        EdgeView ev = edgeViews.get(u + "-" + v);
        if (ev == null) ev = edgeViews.get(v + "-" + u);
        return ev;
    }
}