package ui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.Edge;
import model.Graph;
import model.Vertex;
import ui.components.EdgeView;
import ui.components.VertexNode;

import java.util.HashMap;
import java.util.Map;

public class GraphPane extends Pane {
    private final Graph graph;

    // Map quản lý
    private final Map<Integer, VertexNode> vertexNodes = new HashMap<>();
    private final Map<String, EdgeView> edgeViews = new HashMap<>();

    // === MỚI: Tách lớp để quản lý thứ tự vẽ ===
    private final Group edgeGroup = new Group(); // Lớp dưới
    private final Group nodeGroup = new Group(); // Lớp trên

    public GraphPane(Graph graph) {
        this.graph = graph;

        // Set style background màu trắng
        setStyle("-fx-background-color: white; -fx-border-color: #cfd8dc; -fx-border-width: 1px;");

        // Vẽ nền kẻ ô (Grid)
        drawGrid();

        getChildren().addAll(edgeGroup, nodeGroup);
    }

    private void drawGrid() {
        // Dùng Canvas để vẽ lưới cho hiệu năng cao, hoặc đơn giản là set background image css
        // Cách đơn giản nhất trong Java code:
        // Ta dùng CSS background-image
        this.setStyle(
                "-fx-background-color: white, " +
                        "linear-gradient(from 0.5px 0px to 10.5px 0px, repeat, #f0f0f0 5%, transparent 5%), " +
                        "linear-gradient(from 0px 0.5px to 0px 10.5px, repeat, #f0f0f0 5%, transparent 5%);"
        );
    }

    public void drawFromGraph() {
        // Clear dữ liệu trong các group con
        edgeGroup.getChildren().clear();
        nodeGroup.getChildren().clear();

        vertexNodes.clear();
        edgeViews.clear();

        // 1. Tạo VertexNode -> Add vào nodeGroup
        for (Vertex v : graph.getVertices()) {
            VertexNode node = new VertexNode(v);
            vertexNodes.put(v.id, node);
            nodeGroup.getChildren().add(node); // Add vào lớp trên
        }

        // 2. Tạo EdgeView -> Add vào edgeGroup
        for (Edge e : graph.getEdges()) {
            VertexNode uNode = vertexNodes.get(e.from);
            VertexNode vNode = vertexNodes.get(e.to);

            if (uNode == null || vNode == null) continue;

            EdgeView ev = new EdgeView(uNode, vNode, e, graph.isDirected(), graph.isWeighted());

            edgeViews.put(e.from + "-" + e.to, ev);
            if (!graph.isDirected()) {
                edgeViews.put(e.to + "-" + e.from, ev);
            }

            // Add các phần tử của Edge vào lớp dưới
            edgeGroup.getChildren().add(ev.getLine());
            if (graph.isDirected()) edgeGroup.getChildren().add(ev.getArrow());
            if (ev.getLabel() != null) edgeGroup.getChildren().add(ev.getLabel());
        }
    }

    // Các hàm highlight, getEdge, getNodes giữ nguyên...
    public EdgeView getEdge(int u, int v) {
        EdgeView ev = edgeViews.get(u + "-" + v);
        if (ev == null) ev = edgeViews.get(v + "-" + u);
        return ev;
    }

    public Map<Integer, VertexNode> getNodes() {
        return vertexNodes;
    }

    public void resetVisual() {
        vertexNodes.values().forEach(VertexNode::resetColor);
        edgeViews.values().forEach(EdgeView::resetStyle);
    }

    // ... Copy lại các hàm highlightVisit, highlightPathNode, highlightEdge ở bài trước
    public void highlightPathNode(int u) {
        VertexNode n = vertexNodes.get(u);
        if (n != null) n.highlightResult();
    }

    public void highlightEdge(int u, int v, Color color) {
        EdgeView ev = getEdge(u, v);
        if (ev != null) ev.setHighlight(color); // Hàm này trong EdgeView gọi toFront(), nhưng chỉ toFront trong edgeGroup thôi
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
}