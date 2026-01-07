package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.controls.AlgoControlPane;
import ui.controls.GraphInputPane;

public class ControlPane extends VBox {

    public ControlPane(MainApp app, GraphInputPane inputPane, AlgoControlPane algoPane) {
        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(320);

        Button backBtn = new Button("← Back to Setup");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-base: #ffcccc;");
        backBtn.setOnAction(e -> {
            app.showSetup();
        });

        getChildren().addAll(
                backBtn,
                new Separator(),
                inputPane,
                new Separator(),
                algoPane
        );

        VBox.setVgrow(algoPane, Priority.ALWAYS);
    }
}