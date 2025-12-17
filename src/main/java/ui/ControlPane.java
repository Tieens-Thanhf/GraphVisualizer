package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import model.Graph;
import ui.controls.AlgoControlPane;
import ui.controls.GraphInputPane;

public class ControlPane extends VBox {

    private final MainApp app;
    private final Graph graph;
    private final GraphPane graphPane;

    private AlgoControlPane algoControlPane;

    public ControlPane(MainApp app, Graph graph, GraphPane graphPane) {
        this.app = app;
        this.graph = graph;
        this.graphPane = graphPane;

        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(280);

        initUI();
    }

    private void initUI() {
        Button backBtn = new Button("← Back to Setup");
        backBtn.setOnAction(e -> {
            graph.clear();
            graphPane.clear();
            app.showSetup();
        });

        algoControlPane = new AlgoControlPane(graph, graphPane);

        GraphInputPane graphInputPane = new GraphInputPane(graph, graphPane, () -> {
            algoControlPane.refreshAlgoList();
        });

        getChildren().addAll(
                backBtn,
                new Separator(),
                graphInputPane,
                new Separator(),
                algoControlPane
        );
    }
}