package algorithm;

import model.Graph;
import java.util.function.Predicate;

public enum AlgorithmType {
    BFS("Breadth-First Search", g -> true, true, false),

    DFS("Depth-First Search", g -> true, true, false),

    DIJKSTRA("Dijkstra Shortest Path",
            g -> g.isWeighted() && g.hasNegativeWeights(),
            true, true),

    KRUSKAL("Kruskal MST",
            g -> g.isWeighted() && !g.isDirected(),
            false, false),

    PRIM("Prim MST",
            g -> g.isWeighted() && !g.isDirected(),
            true, false),

    TARJAN("Tarjan SCC",
            Graph::isDirected,
            false, false),

    MAX_FLOW("Max Flow (Edmonds-Karp)",
            g -> g.isWeighted() && g.isDirected() && g.hasNegativeWeights(),
            true, true),

    GBFS("Greedy Best-First Search",
            Graph::isWeighted,
            true, true),

    ASTAR("A* Search",
            g -> g.isWeighted() && g.hasNegativeWeights(),
            true, true);

    private final String displayName;
    private final Predicate<Graph> condition;
    private final boolean requiresStart;
    private final boolean requiresTarget;

    AlgorithmType(String displayName, Predicate<Graph> condition, boolean requiresStart, boolean requiresTarget) {
        this.displayName = displayName;
        this.condition = condition;
        this.requiresStart = requiresStart;
        this.requiresTarget = requiresTarget;
    }

    public boolean isCompatible(Graph g) { return condition.test(g); }
    public boolean isStartRequired() { return requiresStart; }
    public boolean isTargetRequired() { return requiresTarget; }

    @Override
    public String toString() { return displayName; }
}