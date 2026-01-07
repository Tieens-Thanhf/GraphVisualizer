package ui.formatter;

import algorithm.AlgoStep;
import model.Graph;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultFormatter implements ResultFormatter {
    @Override
    public String format(Graph graph, List<AlgoStep> steps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Thứ tự duyệt:\n");
        String path = steps.stream()
                .filter(s -> s.type == AlgoStep.Type.VISIT_VERTEX)
                .map(s -> String.valueOf(s.u))
                .distinct()
                .collect(Collectors.joining(" -> "));
        sb.append(path);
        return sb.toString();
    }
}