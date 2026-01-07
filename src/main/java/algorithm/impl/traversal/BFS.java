package algorithm.impl.traversal;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Graph;
import model.Edge;
import java.util.*;

public class BFS implements GraphAlgorithm {

    private final Graph g;
    private final int start;
    private final boolean[] visited;
    private final List<AlgoStep> steps = new ArrayList<>();

    public BFS(Graph g, int start) {
        this.g = g;
        this.start = start;
        visited = new boolean[g.vertexCount() + 1];
    }

    public List<AlgoStep> run() {
        steps.clear();

        Queue<Integer> q = new LinkedList<>();

        visited[start] = true;
        q.add(start);

        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, start, -1));

        while (!q.isEmpty()) {
            int u = q.poll();

            List<Edge> neighbors = g.getAdj(u);

            neighbors.sort(Comparator.comparingInt(e -> e.to));

            for (Edge e : neighbors) {
                int v = e.to;
                steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, u, v));

                if (!visited[v]) {
                    visited[v] = true;

                    steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, v, u)); // u là parent
                    q.add(v);
                }
            }

            steps.add(new AlgoStep(AlgoStep.Type.FINISH_VERTEX, u, -1));
        }

        return steps;
    }
}