package ui.formatter;

import algorithm.AlgoStep;
import model.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ConnectivityFormatter implements ResultFormatter {
    @Override
    public String format(Graph graph, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Các thành phần liên thông mạnh (SCC):\n");
        sb.append("-------------------------------------\n");
        Map<Integer, List<Integer>> sccMap = new TreeMap<>();

        for (AlgoStep s : steps) {
            if (s.type == AlgoStep.Type.FOUND_SCC) {
                sccMap.computeIfAbsent(s.v, k -> new ArrayList<>()).add(s.u);
            }
        }

        sb.append("Tổng số vùng: ").append(sccMap.size()).append("\n\n");

        sccMap.forEach((id, nodes) -> {
            sb.append("Vùng ").append(id + 1).append(": { ")
                    .append(nodes.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                    .append(" }\n");
        });

        return sb.toString();
    }
}