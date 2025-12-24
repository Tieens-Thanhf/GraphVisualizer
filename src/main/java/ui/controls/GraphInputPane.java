package ui.controls;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Graph;
import ui.GraphPane;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class GraphInputPane extends VBox {

    private final Graph graph;
    private final GraphPane graphPane;
    private final Runnable onGraphUpdate;

    private TextField vertexField;
    private TextArea edgeArea;
    private Button createBtn;

    // Khai báo thêm 2 nút
    private Button saveBtn;
    private Button loadBtn;

    public GraphInputPane(Graph graph, GraphPane graphPane, Runnable onGraphUpdate) {
        this.graph = graph;
        this.graphPane = graphPane;
        this.onGraphUpdate = onGraphUpdate;

        setSpacing(10);
        setPadding(new Insets(10)); // Tăng padding lên chút cho thoáng

        // Thêm class CSS nếu bạn đang dùng file style.css
        getStyleClass().add("control-pane");

        initUI();
    }

    private void initUI() {
        vertexField = new TextField();
        vertexField.setPromptText("Số đỉnh (n)");
        vertexField.getStyleClass().add("text-field");

        edgeArea = new TextArea();
        edgeArea.setEditable(false);
        edgeArea.setPromptText("Nhập số đỉnh trước");
        edgeArea.setPrefRowCount(8); // Tăng chiều cao để nhìn được nhiều cạnh hơn
        edgeArea.getStyleClass().add("text-area");

        // Logic cũ: Khi nhập số đỉnh thì mở khóa ô nhập cạnh
        vertexField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int n = Integer.parseInt(newVal.trim());
                if (n <= 0) throw new NumberFormatException();

                edgeArea.setEditable(true);
                // Giữ nguyên text cũ nếu đang load file, chỉ đổi prompt
                if (edgeArea.getText().isEmpty()) {
                    edgeArea.clear();
                }
                String prompt = graph.isWeighted() ? "Nhập: u v w (VD: 1 2 5)" : "Nhập: u v (VD: 1 2)";
                edgeArea.setPromptText(prompt);
            } catch (Exception ex) {
                // Nếu số đỉnh lỗi thì khóa lại
                // edgeArea.setEditable(false); // (Optional) Có thể bỏ dòng này để trải nghiệm mượt hơn
            }
        });

        createBtn = new Button("Vẽ Đồ Thị");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> handleCreateGraph());
        createBtn.getStyleClass().add("button");

        Button resetViewBtn = new Button("Reset View");
        resetViewBtn.setMaxWidth(Double.MAX_VALUE);
        resetViewBtn.getStyleClass().add("button");
        resetViewBtn.setOnAction(e -> graphPane.resetView());
        HBox.setHgrow(resetViewBtn, Priority.ALWAYS);
        HBox toolsBox = new HBox(10, resetViewBtn);

        saveBtn = new Button("Lưu File");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.getStyleClass().add("button");
        saveBtn.setOnAction(e -> handleSaveFile());
        HBox.setHgrow(saveBtn, Priority.ALWAYS); // Giãn đều

        loadBtn = new Button("Mở File");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.getStyleClass().add("button");
        loadBtn.setOnAction(e -> handleLoadFile());
        HBox.setHgrow(loadBtn, Priority.ALWAYS);

        // Gom 2 nút vào một hàng ngang (HBox)
        HBox fileBox = new HBox(10, loadBtn, saveBtn);

        getChildren().addAll(
                new Label("File Dữ liệu:"),
                fileBox,
                new Separator(),
                new Label("Số đỉnh:"), vertexField,
                new Label("Danh sách cạnh:"), edgeArea,
                createBtn,
                new Label("Công cụ:"),
                toolsBox
        );
    }

    // === XỬ LÝ LƯU FILE ===
    private void handleSaveFile() {
        if (vertexField.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Chưa có dữ liệu để lưu!").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file đồ thị");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("graph_data.txt");

        // === MỚI: Mặc định mở tại thư mục chứa Project ===
        // System.getProperty("user.dir") lấy đường dẫn thư mục gốc của dự án
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.exists()) {
            fileChooser.setInitialDirectory(currentDir);
        }
        // ================================================

        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(vertexField.getText().trim());
                writer.println(edgeArea.getText());
                new Alert(Alert.AlertType.INFORMATION, "Đã lưu file thành công!").show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu file: " + ex.getMessage()).show();
            }
        }
    }

    // === XỬ LÝ MỞ FILE ===
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Mở đồ thị");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.exists()) {
            fileChooser.setInitialDirectory(currentDir);
        }

        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            try {
                // Đọc tất cả các dòng
                var lines = Files.readAllLines(file.toPath());

                if (!lines.isEmpty()) {
                    // Dòng 1 là số đỉnh -> Set vào ô vertexField
                    // Listener của vertexField sẽ tự động chạy và unlock ô edgeArea
                    String nStr = lines.get(0).trim();
                    vertexField.setText(nStr);

                    // Các dòng còn lại nối lại và set vào edgeArea
                    String edgesStr = lines.stream()
                            .skip(1) // Bỏ dòng đầu
                            .collect(Collectors.joining("\n"));
                    edgeArea.setText(edgesStr);

                    // Tự động bấm nút Vẽ luôn cho tiện
                    handleCreateGraph();
                }
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi đọc file: " + ex.getMessage()).show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "File sai định dạng!").show();
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

                if (graph.isWeighted()) {
                    // Nếu là đồ thị có trọng số nhưng file nhập thiếu trọng số -> mặc định là 1 hoặc báo lỗi
                    if (parts.length >= 3) {
                        w = Integer.parseInt(parts[2]);
                    }
                }

                graph.addEdge(u, v, w);
            }

            // Gọi hàm sắp xếp hình tròn (nếu Graph bạn có hàm này, nếu không thì thôi)
            if (graph instanceof Object) {
                // graph.arrangeCircle(); // Bỏ comment dòng này nếu Graph.java đã có hàm arrangeCircle
            }

            graphPane.drawFromGraph();

            if (onGraphUpdate != null) {
                onGraphUpdate.run();
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi dữ liệu: " + ex.getMessage()).show();
        }
    }
}