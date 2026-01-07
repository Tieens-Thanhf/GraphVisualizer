package ui.formatter;

import algorithm.AlgoStep;
import model.Edge;
import model.Graph;
import java.util.List;

public class TreeFormatter implements ResultFormatter {
    @Override
    public String format(Graph graph, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Minimum Spanning Tree (MST):\n");
        int totalWeight = 0;
        var mstSteps = steps.stream().filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_PATH).toList();

        for (AlgoStep s : mstSteps) {
            int w = 0;
            for(Edge e : graph.getAdj(s.u)) if(e.to == s.v) w = e.weight;
            totalWeight += w;
            sb.append(String.format("Cạnh (%d - %d) : %d\n", s.u, s.v, w));
        }
        sb.append("----------------\nTổng trọng số: ").append(totalWeight);
        return sb.toString();
    }
}