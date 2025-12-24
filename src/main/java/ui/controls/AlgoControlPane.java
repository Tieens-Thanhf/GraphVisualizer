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

    private VBox startInputContainer;
    private TextField startVertexField;

    private VBox targetInputContainer;
    private TextField targetVertexField;

    private Button runBtn; // Chỉ giữ lại nút Run
    private TextArea resultArea;

    public AlgoControlPane(Graph graph, GraphPane graphPane) {
        this.graph = graph;
        this.graphPane = graphPane;
        setSpacing(10);
        setPadding(new Insets(10));

        getStyleClass().add("control-pane");

        initUI();
    }

    private void initUI() {
        // 1. Setup Start Node
        startVertexField = new TextField();
        startVertexField.setPromptText("ID Đỉnh bắt đầu");
        startVertexField.getStyleClass().add("text-field");
        startInputContainer = new VBox(5, new Label("Start Node:"), startVertexField);

        // 2. Setup Target Node
        targetVertexField = new TextField();
        targetVertexField.setPromptText("ID Đỉnh đích");
        targetVertexField.getStyleClass().add("text-field");
        targetInputContainer = new VBox(5, new Label("Target Node:"), targetVertexField);
        targetInputContainer.setVisible(false);
        targetInputContainer.setManaged(false);

        // 3. ComboBox
        algoComboBox = new ComboBox<>();
        algoComboBox.setPrefWidth(Double.MAX_VALUE);
        algoComboBox.getStyleClass().add("combo-box");
        algoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateInputFields(newVal);
        });

        refreshAlgoList();

        // 4. Button Run (Đã xóa nút Reset Flow, trả về giao diện gọn gàng)
        runBtn = new Button("Run Algorithm");
        runBtn.setPrefWidth(Double.MAX_VALUE);
        runBtn.getStyleClass().add("button");
        runBtn.setOnAction(e -> runAlgorithm());

        // 5. Result Area
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(6);
        resultArea.setWrapText(true);
        resultArea.getStyleClass().add("text-area");

        getChildren().addAll(
                new Label("Chọn thuật toán:"), algoComboBox,
                startInputContainer,
                targetInputContainer,
                runBtn, // Add trực tiếp runBtn, không qua HBox nữa
                new Label("Kết quả:"), resultArea
        );
    }

    private void updateInputFields(AlgorithmType type) {
        boolean needTarget = type.isTargetRequired();
        targetInputContainer.setVisible(needTarget);
        targetInputContainer.setManaged(needTarget);
        if (!needTarget) targetVertexField.clear();

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
            updateInputFields(algoComboBox.getValue());
        }
    }

    private void runAlgorithm() {
        if (graph.vertexCount() == 0) return;

        try {
            AlgorithmType type = algoComboBox.getValue();
            if (type == null) return;

            // === LOGIC TỰ ĐỘNG RESET LUỒNG ===
            // Nếu thuật toán KHÔNG phải là MaxFlow, ta phải dọn dẹp hiện trường
            // để các text dạng "5/10" trở về "10" bình thường.
            if (type != AlgorithmType.MAX_FLOW) {
                graph.resetFlow();          // Reset dữ liệu model về 0
                graphPane.drawFromGraph();  // Vẽ lại EdgeView để hiển thị Weight gốc
            }
            // Ngược lại (type == MAX_FLOW): Ta KHÔNG reset, để giữ tính năng cộng dồn luồng.

            int start = -1;
            int target = -1;

            if (type.isStartRequired()) {
                String sText = startVertexField.getText().trim();
                if (sText.isEmpty()) { showAlert("Vui lòng nhập Đỉnh bắt đầu!"); return; }
                start = Integer.parseInt(sText);
            } else {
                start = 1;
            }

            if (type.isTargetRequired()) {
                String tText = targetVertexField.getText().trim();
                if (tText.isEmpty()) { showAlert("Vui lòng nhập Đỉnh đích!"); return; }
                target = Integer.parseInt(tText);
            }

            GraphAlgorithm algo = AlgoFactory.createAlgorithm(type, graph, start, target);
            var steps = algo.run();

            // Reset Visual (chỉ xóa màu đỏ/xanh, không xóa text trên cạnh)
            graphPane.resetVisual();

            AlgoAnimator animator = new AlgoAnimator(steps, graphPane);
            animator.play();

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

        // Check MaxFlow Result
        var resultStep = steps.stream()
                .filter(s -> s.type == AlgoStep.Type.SHOW_RESULT)
                .findFirst();

        if (resultStep.isPresent()) {
            sb.append("=== KẾT QUẢ ===\n");
            sb.append(resultStep.get().extraData).append("\n\n");
        }

        if (type == AlgorithmType.KRUSKAL) {
            sb.append("Minimum Spanning Tree (Kruskal):\n");
            int totalWeight = 0;
            var mstSteps = steps.stream()
                    .filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_PATH)
                    .toList();
            for (AlgoStep s : mstSteps) {
                int w = getEdgeWeight(s.u, s.v);
                totalWeight += w;
                sb.append(String.format("Cạnh (%d - %d) : %d\n", s.u, s.v, w));
            }
            sb.append("----------------\n");
            sb.append("Tổng trọng số: ").append(totalWeight);

        } else if (type == AlgorithmType.DIJKSTRA
                || type == AlgorithmType.ASTAR
                || type == AlgorithmType.GBFS) {

            List<Integer> path = steps.stream()
                    .filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_NODE)
                    .map(s -> s.u)
                    .toList();

            if (!path.isEmpty()) {
                sb.append("Đường đi tìm được: ");
                String pathStr = path.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" -> "));
                sb.append(pathStr);

                int totalCost = calculatePathCost(path);
                sb.append("\nTổng chi phí: ").append(totalCost);

            } else {
                sb.append("Không tìm thấy đường đi.");
            }

        } else if (type == AlgorithmType.TARJAN) {
            sb.append("Strongly Connected Components (Tarjan):\n");
            Map<Integer, List<Integer>> sccMap = new TreeMap<>();
            for (AlgoStep s : steps) {
                if (s.type == AlgoStep.Type.FOUND_SCC) {
                    sccMap.computeIfAbsent(s.v, k -> new ArrayList<>()).add(s.u);
                }
            }
            sb.append("Tổng số vùng liên thông: ").append(sccMap.size()).append("\n");
            sccMap.forEach((id, nodes) -> {
                sb.append("Vùng ").append(id + 1).append(": { ")
                        .append(nodes.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                        .append(" }\n");
            });

        } else if (type == AlgorithmType.MAX_FLOW) {
            sb.append("Chi tiết đường tăng luồng:\n");
            sb.append("Màu đỏ thể hiện đường đi của luồng nước.");

        } else {
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
            cost += getEdgeWeight(pathNodes.get(i), pathNodes.get(i + 1));
        }
        return cost;
    }

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