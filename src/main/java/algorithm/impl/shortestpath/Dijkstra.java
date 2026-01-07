package algorithm.impl.shortestpath;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import java.util.*;

public class Dijkstra implements GraphAlgorithm {

    private final Graph graph;
    private final int startNode;
    private final int targetNode;
    private final List<AlgoStep> steps;

    public Dijkstra(Graph graph, int startNode, int targetNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.targetNode = targetNode;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        int n = graph.vertexCount();

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[startNode] = 0;

        int[] parent = new int[n + 1];
        Arrays.fill(parent, -1);

        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.dist));
        pq.add(new NodeDist(startNode, 0));

        boolean[] finalized = new boolean[n + 1];
        boolean targetFound = false;

        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, startNode, -1));

        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            int u = current.id;

            if (finalized[u]) continue;
            finalized[u] = true;

            if (u != startNode) {
                steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, parent[u]));
            }

            steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, parent[u]));

            if (u == targetNode) {
                targetFound = true;
                break;
            }

            for (Edge e : graph.getAdj(u)) {
                int v = e.to;
                int weight = e.weight;

                steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, u, v));

                if (!finalized[v] && dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;
                    pq.add(new NodeDist(v, dist[v]));
                }
            }
            steps.add(new AlgoStep(AlgoStep.Type.FINISH_VERTEX, u, -1));
        }

        if (targetFound) {
            reconstructPath(parent, targetNode);
        }

        return steps;
    }

    private void reconstructPath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);

        if (!path.isEmpty()) {
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, path.get(0), -1));
        }

        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);

            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, u, v));
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, v, -1));
        }
    }

    private static class NodeDist {
        int id, dist;
        public NodeDist(int id, int dist) { this.id = id; this.dist = dist; }
    }
}