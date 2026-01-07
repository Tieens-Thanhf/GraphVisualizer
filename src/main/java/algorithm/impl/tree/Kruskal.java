package algorithm.impl.tree;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import java.util.*;

public class Kruskal implements GraphAlgorithm {

    private final Graph graph;
    private final List<AlgoStep> steps;

    public Kruskal(Graph graph) {
        this.graph = graph;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        int n = graph.vertexCount();

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(Comparator.comparingInt(e -> e.weight));
        DSU dsu = new DSU(n);

        int edgesCount = 0;

        for (Edge e : edges) {
            steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, e.from, e.to));

            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);

                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, e.from, e.to));
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, e.from, -1));
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, e.to, -1));

                edgesCount++;
            }
            if (edgesCount == n - 1) break;
        }

        return steps;
    }

    private static class DSU {
        private final int[] parent;

        public DSU(int n) {
            parent = new int[n + 1];
            for (int i = 1; i <= n; i++) parent[i] = i;
        }

        public int find(int u) {
            if (parent[u] == u) return u;
            return parent[u] = find(parent[u]);
        }

        public void union(int u, int v) {
            int rootU = find(u);
            int rootV = find(v);
            if (rootU != rootV) {
                parent[rootV] = rootU;
            }
        }
    }
}