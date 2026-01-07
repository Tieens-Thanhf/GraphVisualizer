package ui.formatter;

import algorithm.AlgoStep;
import model.Edge;
import model.Graph;
import java.util.List;

public class FlowFormatter implements ResultFormatter {
    @Override
    public String format(Graph graph, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();

        sb.append("Trạng thái luồng trên mạng (Flow / Capacity):\n");
        sb.append("------------------------------------------\n");

        boolean hasFlow = false;
        for (Edge e : graph.getEdges()) {
            if (e.currentFlow > 0) {
                sb.append(String.format("Cạnh %d -> %d : %d / %d\n",
                        e.from, e.to, e.currentFlow, e.weight));
                hasFlow = true;
            }
        }

        if (!hasFlow) {
            sb.append("(Chưa có luồng nào được đẩy qua mạng)\n");
        }

        return sb.toString();
    }
}