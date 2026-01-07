package ui.formatter;

import algorithm.AlgoStep;
import model.Edge;
import model.Graph;
import java.util.List;
import java.util.stream.Collectors;

public class PathfindingFormatter implements ResultFormatter {
    @Override
    public String format(Graph graph, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();
        List<Integer> path = steps.stream()
                .filter(s -> s.type == AlgoStep.Type.HIGHLIGHT_NODE)
                .map(s -> s.u)
                .toList();

        if (!path.isEmpty()) {
            sb.append("Đường đi tìm được: ");
            sb.append(path.stream().map(String::valueOf).collect(Collectors.joining(" -> ")));
            sb.append("\nTổng chi phí: ").append(calculateCost(graph, path));
        } else {
            sb.append("Không tìm thấy đường đi.");
        }
        return sb.toString();
    }

    private int calculateCost(Graph graph, List<Integer> path) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            for (Edge e : graph.getAdj(u)) {
                if (e.to == v) { cost += e.weight; break; }
            }
        }
        return cost;
    }
}