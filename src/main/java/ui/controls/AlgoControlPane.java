package ui.controls;

import algorithm.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Edge;
import model.Graph;
import ui.GraphPane;
import ui.animation.AlgoAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AlgoControlPane extends VBox {

    private final Graph graph;
    private final GraphPane graphPane;

    private ComboBox<AlgorithmType> algoComboBox;

    // Đóng gói các ô nhập vào VBox để ẩn/hiện cả cụm (Label + TextField)
    private VBox startInputContainer;
    private TextField startVertexField;

    private VBox targetInputContainer;
    private TextField targetVertexField;

    private Button runBtn;
    private TextArea resultArea;

    public AlgoControlPane(Graph graph, GraphPane graphPane) {
        this.graph = graph;
        this.graphPane = graphPane;
        setSpacing(10);
        setPadding(new Insets(10));
        initUI();
    }

    private void initUI() {
        // 1. Setup Start Node Container
        startVertexField = new TextField();
        startVertexField.setPromptText("ID Đỉnh bắt đầu");
        startInputContainer = new VBox(5, new Label("Start Node:"), startVertexField);

        // 2. Setup Target Node Container
        targetVertexField = new TextField();
        targetVertexField.setPromptText("ID Đỉnh đích");
        targetInputContainer = new VBox(5, new Label("Target Node:"), targetVertexField);
        // Mặc định ẩn Target
        targetInputContainer.setVisible(false);
        targetInputContainer.setManaged(false);

        // 3. Setup ComboBox
        algoComboBox = new ComboBox<>();
        algoComboBox.setPrefWidth(Double.MAX_VALUE);
        algoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateInputFields(newVal);
        });

        refreshAlgoList();

        // 4. Các nút còn lại
        runBtn = new Button("Run Algorithm");
        runBtn.setPrefWidth(Double.MAX_VALUE);
        runBtn.setOnAction(e -> runAlgorithm());

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(6); // Tăng lên chút để hiển thị MST
        resultArea.setWrapText(true);

        getChildren().addAll(
                new Label("Chọn thuật toán:"), algoComboBox,
                startInputContainer,  // Add container
                targetInputContainer, // Add container
                runBtn,
                new Label("Kết quả:"), resultArea
        );
    }

    private void updateInputFields(AlgorithmType type) {
        // Xử lý ẩn hiện Target (như cũ)
        boolean needTarget = type.isTargetRequired();
        targetInputContainer.setVisible(needTarget);
        targetInputContainer.setManaged(needTarget);
        if (!needTarget) targetVertexField.clear();

        // Xử lý ẩn hiện Start (MỚI)
        boolean needStart = type.isStartRequired();
        startInputContainer.setVisible(needStart);
        startInputContainer.setManaged(needStart);
        if (!needStart) startVertexField.clear();
    }

    public void refreshAlgoList() {
        algoComboBox.getItems().clear();
        for (AlgorithmType type : AlgorithmType.values()) {
            if (type.isCompatible(graph)) algoComboBox.getItems().add(type);
        }
        if (!algoComboBox.getItems().isEmpty()) {
            algoComboBox.getSelectionModel().selectFirst();
            updateInputFields(algoComboBox.getValue()); // Update ngay lần đầu
        }
    }

    private void runAlgorithm() {
        if (graph.vertexCount() == 0) return;

        try {
            AlgorithmType type = algoComboBox.getValue();
            if (type == null) return;

            int start = -1;
            int target = -1;

            // Chỉ lấy input nếu thuật toán yêu cầu
            if (type.isStartRequired()) {
                String sText = startVertexField.getText().trim();
                if (sText.isEmpty()) { showAlert("Vui lòng nhập Đỉnh bắt đầu!"); return; }
                start = Integer.parseInt(sText);
            }
            // Nếu thuật toán không cần start (VD: Kruskal), ta truyền đại số 1 vào
            // để Factory không bị lỗi, nhưng thuật toán sẽ không dùng đến nó.
            else {
                start = 1;
            }

            if (type.isTargetRequired()) {
                String tText = targetVertexField.getText().trim();
                if (tText.isEmpty()) { showAlert("Vui lòng nhập Đỉnh đích!"); return; }
                target = Integer.parseInt(tText);
            }

            // Chạy thuật toán
            GraphAlgorithm algo = AlgoFactory.createAlgorithm(type, graph, start, target);
            var steps = algo.run();

            // Animation
            graphPane.resetVisual();
            AlgoAnimator animator = new AlgoAnimator(steps, graphPane);
            animator.play();

            // === HIỂN THỊ KẾT QUẢ (LOGIC MỚI) ===
            printResult(type, steps);

        } catch (NumberFormatException e) {
            showAlert("Vui lòng nhập đúng định dạng số!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi: " + e.getMessage());
        }
    }

    private void printResult(AlgorithmType type, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();

        if (type == AlgorithmType.KRUSKAL) {
            // === XỬ LÝ RIÊNG CHO MST ===
            sb.append("Minimum Spanning Tree (Kruskal):\n");
            int totalWeight = 0;

            // Lọc ra các bước chọn cạnh (HIGHLIGHT_PATH)
            var mstSteps = steps.stream()
                    .filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_PATH)
                    .toList();

            for (AlgoStep s : mstSteps) {
                // Tìm trọng số của cạnh u-v trong đồ thị để cộng tổng
                int w = getEdgeWeight(s.u, s.v);
                totalWeight += w;
                sb.append(String.format("Cạnh (%d - %d) : %d\n", s.u, s.v, w));
            }
            sb.append("----------------\n");
            sb.append("Tổng trọng số: ").append(totalWeight);

        }else if (type == AlgorithmType.DIJKSTRA) {
            // === LOGIC MỚI CHO DIJKSTRA ===

            // 1. Lọc ra danh sách các đỉnh được tô màu ĐỎ (HIGHLIGHT_NODE)
            // Vì thuật toán tô màu theo thứ tự từ Start -> Target, nên list này chính là đường đi.
            List<Integer> path = steps.stream()
                    .filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_NODE)
                    .map(s -> s.u)
                    .toList(); // Java 16+, nếu Java thấp hơn dùng .collect(Collectors.toList())

            if (!path.isEmpty()) {
                sb.append("Đường đi ngắn nhất: ");

                // Nối các đỉnh lại bằng dấu mũi tên
                String pathStr = path.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" -> "));
                sb.append(pathStr);

                // Tính tổng trọng số (Optional)
                int totalCost = calculatePathCost(path);
                sb.append("\nTổng chi phí: ").append(totalCost);

            } else {
                sb.append("Không tìm thấy đường đi hoặc chưa chạy xong.");
            }

        } else if (type == AlgorithmType.TARJAN) {
            sb.append("Strongly Connected Components (Tarjan):\n");

            // Gom các đỉnh theo SCC ID
            // Map<SCC_ID, List<Node_ID>>
            Map<Integer, List<Integer>> sccMap = new TreeMap<>();

            for (AlgoStep s : steps) {
                if (s.type == AlgoStep.Type.FOUND_SCC) {
                    int nodeId = s.u;
                    int sccId = s.v;
                    sccMap.computeIfAbsent(sccId, k -> new ArrayList<>()).add(nodeId);
                }
            }

            sb.append("Tổng số vùng liên thông: ").append(sccMap.size()).append("\n");

            sccMap.forEach((id, nodes) -> {
                sb.append("Vùng ").append(id + 1).append(": { ") // +1 cho đẹp
                        .append(nodes.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                        .append(" }\n");
            });
        } else {
            // === XỬ LÝ MẶC ĐỊNH (BFS/DFS) ===
            sb.append("Thứ tự duyệt:\n");
            String path = steps.stream()
                    .filter(s -> s.type == AlgoStep.Type.VISIT_VERTEX)
                    .map(s -> String.valueOf(s.u))
                    .distinct()
                    .collect(Collectors.joining(" -> "));
            sb.append(path);
        }

        resultArea.setText(sb.toString());
    }

    private int calculatePathCost(List<Integer> pathNodes) {
        int cost = 0;
        for (int i = 0; i < pathNodes.size() - 1; i++) {
            int u = pathNodes.get(i);
            int v = pathNodes.get(i + 1);
            cost += getEdgeWeight(u, v);
        }
        return cost;
    }

    // Hàm phụ trợ để lấy trọng số cạnh (Dùng cho in kết quả Kruskal)
    private int getEdgeWeight(int u, int v) {
        for (Edge e : graph.getAdj(u)) {
            if (e.to == v) return e.weight;
        }
        return 0;
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }
}