package ui.animation;

import algorithm.AlgoStep;
import javafx.scene.paint.Color;
import ui.GraphPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import ui.components.EdgeView;
import ui.components.VertexNode;
import java.util.List;

public class AlgoAnimator {
    private final Timeline timeline;
    private final GraphPane graphPane;

    public AlgoAnimator(List<AlgoStep> steps, GraphPane graphPane) {
        this.graphPane = graphPane;

        timeline = new Timeline();
        Duration time = Duration.ZERO;
        Duration stepTime = Duration.seconds(0.8);

        for (AlgoStep s : steps) {
            time = time.add(stepTime);
            timeline.getKeyFrames().add(
                    new KeyFrame(time, e -> applyStep(s))
            );
        }
    }

    private void applyStep(AlgoStep s) {
        VertexNode uNode = graphPane.getNodes().get(s.u);
        EdgeView edge = (s.v != -1) ? graphPane.getEdge(s.u, s.v) : null;

        switch (s.type) {
            case VISIT_VERTEX -> { if (uNode != null) uNode.highlightVisit(); }
            case FINISH_VERTEX -> { if (uNode != null) uNode.highlightFinish(); }
            case HIGHLIGHT_NODE -> { if (uNode != null) uNode.highlightResult(); }

            case EXPLORE_EDGE -> { if (edge != null) edge.highlightExplore(); }
            case HIGHLIGHT_PATH -> { if (edge != null) edge.highlightSuccess(); }

            case FOUND_SCC -> {
                if (uNode != null) {
                    int sccId = s.v;
                    double hue = (sccId * 137) % 360;
                    uNode.setCustomColor(Color.hsb(hue, 0.8, 0.9));
                }
            }

            case UPDATE_FLOW_TEXT -> {
                if (edge != null && s.extraData != null) {
                    // Update text của Label
                    if (edge.getLabel() != null) {
                        edge.getLabel().setText(s.extraData);
                        edge.getLabel().setFill(Color.BLUE);
                    }
                }
            }

            default -> { }
        }
    }

    public void play() {
        timeline.playFromStart();
    }
}