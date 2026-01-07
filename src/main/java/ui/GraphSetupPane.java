package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.GraphConfig;

import java.util.function.Consumer;

public class GraphSetupPane extends VBox {

    public GraphSetupPane(Consumer<GraphConfig> onConfigReady) {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);

        Label title = new Label("GraphVisualizer");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        CheckBox directedBox = new CheckBox("Directed");
        directedBox.setStyle("-fx-font-size: 14px;");

        CheckBox weightedBox = new CheckBox("Weighted");
        weightedBox.setStyle("-fx-font-size: 14px;");

        Button nextBtn = new Button("Start");
        nextBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        nextBtn.setPrefWidth(200);

        nextBtn.setOnAction(e -> {
            GraphConfig config = new GraphConfig(directedBox.isSelected(), weightedBox.isSelected());

            if (onConfigReady != null) {
                onConfigReady.accept(config);
            }
        });

        getChildren().addAll(title, directedBox, weightedBox, nextBtn);
    }
}