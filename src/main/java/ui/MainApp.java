package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Graph;
import model.GraphConfig;
import java.net.URL;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showSetup();
        stage.show();
    }

    public void showSetup() {
        Scene scene = new Scene(new GraphSetupPane(this), 600, 320);

        loadCSS(scene);

        stage.setScene(scene);
        stage.setTitle("Graph Visualizer - Setup");
    }

    public void startGraphEditor(GraphConfig config) {
        Graph graph = new Graph(config);
        GraphPane graphPane = new GraphPane(graph);
        ControlPane controlPane = new ControlPane(this, graph, graphPane);

        BorderPane root = new BorderPane();
        root.setLeft(controlPane);
        root.setCenter(graphPane);

        Scene scene = new Scene(root, 1100, 700); // Tăng kích thước chút cho đẹp

        loadCSS(scene);

        stage.setScene(scene);
        stage.setTitle("Graph Visualizer - Editor");
        stage.centerOnScreen();
    }

    private void loadCSS(Scene scene) {
        try {
            URL cssUrl = getClass().getResource("/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Cảnh báo: Không tìm thấy file style.css!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}