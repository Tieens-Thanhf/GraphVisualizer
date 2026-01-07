package ui.formatter;

import algorithm.AlgoStep;
import model.Graph;
import java.util.List;

public interface ResultFormatter {
    String format(Graph graph, List<AlgoStep> steps);
}