package ui.controls;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Graph;
import ui.GraphPane;
import ui.layout.CircleLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GraphInputPane extends VBox {

    private final Graph graph;
    private final GraphPane graphPane;
    private final Runnable onGraphUpdate;

    private TextField vertexField;
    private TextArea edgeArea;

    public GraphInputPane(Graph graph, GraphPane graphPane, Runnable onGraphUpdate) {
        this.graph = graph;
        this.graphPane = graphPane;
        this.onGraphUpdate = onGraphUpdate;

        setSpacing(10);
        setPadding(new Insets(10));
        getStyleClass().add("control-pane");

        initUI();
    }

    private void initUI() {
        Label lblTitle = new Label("DỮ LIỆU ĐỒ THỊ");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        vertexField = new TextField();
        vertexField.setPromptText("Số đỉnh (n)");
        vertexField.getStyleClass().add("text-field");

        edgeArea = new TextArea();
        edgeArea.setEditable(false);
        edgeArea.setPromptText("Nhập số đỉnh trước...");
        edgeArea.setPrefRowCount(12);
        edgeArea.setWrapText(true);
        edgeArea.getStyleClass().add("text-area");

        vertexField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int n = Integer.parseInt(newVal.trim());
                if (n <= 0) throw new NumberFormatException();
                edgeArea.setEditable(true);
                if (edgeArea.getText().isEmpty()) edgeArea.clear();
                String prompt = graph.isWeighted() ? "u v w (VD: 1 2 5)" : "u v (VD: 1 2)";
                edgeArea.setPromptText(prompt);
            } catch (Exception _) {}
        });

        Button createBtn = new Button("CẬP NHẬT ĐỒ THỊ");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-base: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> handleCreateGraph());

        getChildren().addAll(
                lblTitle,
                new Label("Số đỉnh:"), vertexField,
                new Label("Danh sách cạnh:"), edgeArea,
                createBtn
        );
    }

    public void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Mở đồ thị");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.exists()) fileChooser.setInitialDirectory(currentDir);

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                List<String> lines = ui.GraphFileUtils.readLines(file);
                if (!lines.isEmpty()) {
                    vertexField.setText(lines.getFirst().trim());
                    String edgesStr = lines.stream().skip(1).collect(Collectors.joining("\n"));
                    edgeArea.setText(edgesStr);
                    handleCreateGraph();
                }
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi đọc file: " + ex.getMessage()).show();
            }
        }
    }

    public void saveToFile() {
        if (vertexField.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Chưa có dữ liệu!").show();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu đồ thị");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("graph_data.txt");
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.exists()) fileChooser.setInitialDirectory(currentDir);

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ui.GraphFileUtils.saveGraphToFile(file, vertexField.getText().trim(), edgeArea.getText());
                new Alert(Alert.AlertType.INFORMATION, "Đã lưu thành công!").show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi lưu file: " + ex.getMessage()).show();
            }
        }
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
                if (graph.isWeighted() && parts.length >= 3) {
                    w = Integer.parseInt(parts[2]);
                }
                graph.addEdge(u, v, w);
            }
            new CircleLayout().arrange(graph.getVertices());
            graphPane.drawFromGraph();
            if (onGraphUpdate != null) onGraphUpdate.run();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi dữ liệu: " + ex.getMessage()).show();
        }
    }
}