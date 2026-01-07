package ui;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import model.Graph;
import ui.controls.AlgoControlPane;
import ui.controls.GraphInputPane;

public class MainLayout extends BorderPane {

    private final GraphPane graphPane;
    private final ControlPane controlPane;
    private final MainToolBar toolBar;

    public MainLayout(MainApp mainApp, Graph graph) {
        this.graphPane = new GraphPane(graph);
        AlgoControlPane algoPane = new AlgoControlPane(graph, this.graphPane);

        GraphInputPane inputPane = new GraphInputPane(graph, this.graphPane, algoPane::refreshAlgoList);

        this.controlPane = new ControlPane(mainApp, inputPane, algoPane);
        this.toolBar = new MainToolBar(graph, this.graphPane, inputPane);
        initLayout();
    }

    private void initLayout() {
        setTop(toolBar);
        setCenter(graphPane);

        ScrollPane leftScroll = new ScrollPane(controlPane);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setStyle("-fx-background-color: transparent;");

        setLeft(leftScroll);
    }
}