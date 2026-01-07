package ui.controls;

import algorithm.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Graph;
import ui.GraphPane;
import ui.animation.AlgoAnimator;
import ui.formatter.FormatterFactory;
import ui.formatter.ResultFormatter;

import java.util.List;


public class AlgoControlPane extends VBox {

    private final Graph graph;
    private final GraphPane graphPane;
    private ComboBox<AlgorithmType> algoComboBox;
    private VBox startInputContainer;
    private TextField startVertexField;
    private VBox targetInputContainer;
    private TextField targetVertexField;
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
        startVertexField = new TextField();
        startVertexField.setPromptText("ID Đỉnh bắt đầu");
        startVertexField.getStyleClass().add("text-field");
        startInputContainer = new VBox(5, new Label("Start Node:"), startVertexField);

        targetVertexField = new TextField();
        targetVertexField.setPromptText("ID Đỉnh đích");
        targetVertexField.getStyleClass().add("text-field");
        targetInputContainer = new VBox(5, new Label("Target Node:"), targetVertexField);
        targetInputContainer.setVisible(false);
        targetInputContainer.setManaged(false);

        algoComboBox = new ComboBox<>();
        algoComboBox.setPrefWidth(Double.MAX_VALUE);
        algoComboBox.getStyleClass().add("combo-box");
        algoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateInputFields(newVal);
        });

        refreshAlgoList();

        Button runBtn = new Button("Run Algorithm");
        runBtn.setPrefWidth(Double.MAX_VALUE);
        runBtn.getStyleClass().add("button");
        runBtn.setOnAction(e -> runAlgorithm());

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(6);
        resultArea.setWrapText(true);
        resultArea.getStyleClass().add("text-area");

        getChildren().addAll(
                new Label("Chọn thuật toán:"), algoComboBox,
                startInputContainer,
                targetInputContainer,
                runBtn,
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

            if (type != AlgorithmType.MAX_FLOW) {
                graph.resetFlow();
                graphPane.drawFromGraph();
            }

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

        var resultStep = steps.stream()
                .filter(s -> s.type == AlgoStep.Type.SHOW_RESULT)
                .findFirst();

        if (resultStep.isPresent()) {
            sb.append("=== KẾT QUẢ ===\n");
            sb.append(resultStep.get().extraData).append("\n\n");
        }

        ResultFormatter formatter = FormatterFactory.getFormatter(type);

        sb.append(formatter.format(graph, steps));

        resultArea.setText(sb.toString());
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }
}