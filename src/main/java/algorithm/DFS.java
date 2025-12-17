package algorithm;

import model.Edge;
import model.Graph;
import java.util.*;

public class DFS implements GraphAlgorithm {

    private final Graph graph;
    private final int startNode;
    private final boolean[] visited;
    private final List<AlgoStep> steps;

    public DFS(Graph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.visited = new boolean[graph.vertexCount() + 1];
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, startNode, -1));
        visited[startNode] = true;
        dfsRecursion(startNode);
        return steps;
    }

    private void dfsRecursion(int u) {
        List<Edge> neighbors = graph.getAdj(u);
        neighbors.sort(Comparator.comparingInt(e -> e.to));

        for (Edge e : neighbors) {
            int v = e.to;
            steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, u, v));

            if (!visited[v]) {
                visited[v] = true;
                steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, v, u));
                dfsRecursion(v);
                steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, -1));
            }
        }
        steps.add(new AlgoStep(AlgoStep.Type.FINISH_VERTEX, u, -1));
    }
}