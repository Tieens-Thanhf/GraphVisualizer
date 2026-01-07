package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Graph;
import model.GraphConfig;

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Graph Algorithms Visualizer");

        showSetup();

        primaryStage.show();
    }

    public void showSetup() {
        GraphSetupPane setupPane = new GraphSetupPane(this::startGraphEditor);

        if (scene == null) {
            scene = new Scene(setupPane, 600, 400);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(setupPane);

            primaryStage.setWidth(600);
            primaryStage.setHeight(400);
            primaryStage.centerOnScreen();
        }
    }

    public void startGraphEditor(GraphConfig config) {
        Graph graph = new Graph(config);

        MainLayout mainLayout = new MainLayout(this, graph);

        scene.setRoot(mainLayout);

        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}