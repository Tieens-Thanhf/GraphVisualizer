package ui.formatter;

import algorithm.AlgorithmType;

public class FormatterFactory {
    public static ResultFormatter getFormatter(AlgorithmType type) {
        switch (type) {
            case DIJKSTRA:
            case ASTAR:
            case GBFS:
                return new PathfindingFormatter();
            case KRUSKAL:
            case TARJAN: return new ConnectivityFormatter();
            case MAX_FLOW: return new FlowFormatter();
            default:
                return new DefaultFormatter();
        }
    }
}