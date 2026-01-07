package ui;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert;
import model.Graph;
import ui.controls.GraphInputPane;
import ui.layout.RandomLayout;

public class MainToolBar extends ToolBar {

    public MainToolBar(Graph graph, GraphPane graphPane, GraphInputPane inputPane) {

        Button btnOpen = new Button("Open File");
        btnOpen.setOnAction(e -> inputPane.loadFromFile());

        Button btnSave = new Button("Save File");
        btnSave.setOnAction(e -> inputPane.saveToFile());

        Button btnResetView = new Button("Reset View");
        btnResetView.setTooltip(new Tooltip("Đưa đồ thị về giữa màn hình"));
        btnResetView.setOnAction(e -> graphPane.resetView());

        Button btnRandom = new Button("Random");
        btnRandom.setOnAction(e -> {
            new RandomLayout().arrange(graph.getVertices());
            graphPane.drawFromGraph();
        });

        Button btnMatrix = new Button("Matrix");
        btnMatrix.setOnAction(e -> showMatrix(graph));

        getItems().addAll(
                btnOpen, btnSave,
                new Separator(),
                btnResetView, btnRandom,
                new Separator(),
                btnMatrix
        );

        setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");
    }

    private void showMatrix(Graph graph) {
        if (graph.vertexCount() == 0) return;
        StringBuilder sb = new StringBuilder("      ");
        int n = graph.vertexCount();
        for (int i = 1; i <= n; i++) sb.append(String.format("%3d ", i));
        sb.append("\n\n");
        for (int i = 1; i <= n; i++) {
            sb.append(String.format("%3d | ", i));
            for (int j = 1; j <= n; j++) {
                int w = 0;
                for (var e : graph.getAdj(i)) if (e.to == j) w = e.weight;
                sb.append(w == 0 ? "  . " : String.format("%3d ", w));
            }
            sb.append("\n");
        }
        TextArea ta = new TextArea(sb.toString());
        ta.setFont(javafx.scene.text.Font.font("Monospaced", 14));
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Ma trận kề");
        a.setHeaderText(null);
        a.getDialogPane().setContent(ta);
        a.setResizable(true);
        a.show();
    }
}