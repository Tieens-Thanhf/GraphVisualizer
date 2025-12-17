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
    private final GraphPane graphPane; // Thay vì chỉ giữ map nodes, ta giữ cả Pane

    // Constructor sửa lại nhận GraphPane
    public AlgoAnimator(List<AlgoStep> steps, GraphPane graphPane) {
        this.graphPane = graphPane;

        timeline = new Timeline();
        Duration time = Duration.ZERO;
        Duration stepTime = Duration.seconds(0.6); // Tốc độ chạy

        for (AlgoStep s : steps) {
            time = time.add(stepTime);
            timeline.getKeyFrames().add(
                    new KeyFrame(time, e -> applyStep(s))
            );
        }
    }

    private void applyStep(AlgoStep s) {
        // Lấy VertexNode từ GraphPane (thông qua getter hoặc map public)
        VertexNode uNode = graphPane.getNodes().get(s.u);

        // Lấy EdgeView (nếu có v != -1)
        EdgeView edge = (s.v != -1) ? graphPane.getEdge(s.u, s.v) : null;

        switch (s.type) {
            // --- XỬ LÝ ĐỈNH ---
            case VISIT_VERTEX -> {
                if (uNode != null) uNode.highlightVisit();
            }
            case FINISH_VERTEX -> {
                if (uNode != null) uNode.highlightFinish();
            }
            case HIGHLIGHT_NODE -> {
                if (uNode != null) uNode.highlightResult();
            }

            // --- XỬ LÝ CẠNH (MỚI) ---
            case EXPLORE_EDGE -> {
                if (edge != null) edge.highlightExplore(); // Xanh dương
            }
            case HIGHLIGHT_PATH -> {
                if (edge != null) edge.highlightSuccess(); // Đỏ đậm
            }

            case FOUND_SCC -> {
                if (uNode != null) {
                    // s.v đang chứa SCC ID (0, 1, 2...)
                    int sccId = s.v;

                    // Sinh màu tự động dựa trên ID
                    // Nhân với một số nguyên tố lớn (vd 137) để các màu kề nhau có độ lệch pha lớn, dễ phân biệt
                    double hue = (sccId * 137) % 360;
                    Color color = Color.hsb(hue, 0.8, 0.9); // Màu tươi, sáng

                    uNode.setCustomColor(color);
                }
            }

            default -> { }
        }
    }

    public void play() {
        timeline.playFromStart();
    }
}