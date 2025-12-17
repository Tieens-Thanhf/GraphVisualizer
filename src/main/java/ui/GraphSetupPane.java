package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.GraphConfig;

public class GraphSetupPane extends VBox {
    public GraphSetupPane(MainApp app) {
        setSpacing(12);
        setPadding(new Insets(16));

        Label title = new Label("Graph Configuration");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        CheckBox directedBox = new CheckBox("Directed graph");
        CheckBox weightedBox = new CheckBox("Weighted graph");

        Button nextBtn = new Button("Create Graph Editor");
        nextBtn.setOnAction(e -> {
            GraphConfig config = new GraphConfig(directedBox.isSelected(), weightedBox.isSelected());
            app.startGraphEditor(config);
        });

        getChildren().addAll(title, directedBox, weightedBox, nextBtn);
    }
}