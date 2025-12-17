package ui.controls;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Graph;
import ui.GraphPane;

public class GraphInputPane extends VBox {

    private final Graph graph;
    private final GraphPane graphPane;
    private final Runnable onGraphUpdate; // <-- Callback: Hành động sẽ chạy sau khi tạo graph

    private TextField vertexField;
    private TextArea edgeArea;
    private Button createBtn;

    public GraphInputPane(Graph graph, GraphPane graphPane, Runnable onGraphUpdate) {
        this.graph = graph;
        this.graphPane = graphPane;
        this.onGraphUpdate = onGraphUpdate;

        setSpacing(10);
        setPadding(new Insets(0, 0, 10, 0)); // Padding dưới
        initUI();
    }

    private void initUI() {
        vertexField = new TextField();
        vertexField.setPromptText("Số đỉnh (n)");

        edgeArea = new TextArea();
        edgeArea.setEditable(false);
        edgeArea.setPromptText("Nhập số đỉnh trước");
        edgeArea.setPrefRowCount(5);

        vertexField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int n = Integer.parseInt(newVal.trim());
                if (n <= 0) throw new NumberFormatException();

                edgeArea.setEditable(true);
                edgeArea.clear();
                String prompt = graph.isWeighted() ? "Nhập: u v w (VD: 1 2 5)" : "Nhập: u v (VD: 1 2)";
                edgeArea.setPromptText(prompt);
            } catch (Exception ex) {
                edgeArea.setEditable(false);
                edgeArea.setPromptText("Số đỉnh không hợp lệ");
            }
        });

        createBtn = new Button("Create Graph");
        createBtn.setPrefWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> handleCreateGraph());

        getChildren().addAll(
                new Label("Số đỉnh:"), vertexField,
                new Label("Danh sách cạnh:"), edgeArea,
                createBtn
        );
    }

    private void handleCreateGraph() {
        try {
            int n = Integer.parseInt(vertexField.getText().trim());
            graph.clear();
            graphPane.clear();
            graph.createVertices(n);

            String[] lines = edgeArea.getText().split("\\n");
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 2) continue;

                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int w = 1;

                if (graph.isWeighted()) {
                    if (parts.length < 3) throw new IllegalArgumentException("Thiếu trọng số ở dòng: " + line);
                    w = Integer.parseInt(parts[2]);
                }

                graph.addEdge(u, v, w);
            }

            graphPane.drawFromGraph();

            if (onGraphUpdate != null) {
                onGraphUpdate.run();
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).show();
        }
    }
}