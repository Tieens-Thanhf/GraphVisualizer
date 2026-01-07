package algorithm.impl.shortestpath;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.*;

public class GBFS implements GraphAlgorithm {

    private final Graph graph;
    private final int start;
    private final int target;
    private final List<AlgoStep> steps;

    public GBFS(Graph graph, int start, int target) {
        this.graph = graph;
        this.start = start;
        this.target = target;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        int n = graph.vertexCount();

        PriorityQueue<NodeCost> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost));
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Vertex targetVertex = graph.getVertex(target);

        pq.add(new NodeCost(start, heuristic(start, targetVertex)));
        visited.add(start);

        boolean found = false;

        while (!pq.isEmpty()) {
            NodeCost current = pq.poll();
            int u = current.id;
            steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, -1));
            if (u == target) {
                found = true;
                break;
            }

            for (Edge e : graph.getAdj(u)) {
                int v = e.to;
                if (!visited.contains(v)) {
                    visited.add(v);
                    parent.put(v, u);

                    // Tính h(v)
                    double h = heuristic(v, targetVertex);
                    pq.add(new NodeCost(v, h));

                    steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, u, v));
                }
            }
        }

        if (found) {
            reconstructPath(parent);
        }
        return steps;
    }

    private double heuristic(int uId, Vertex targetV) {
        Vertex u = graph.getVertex(uId);
        double dx = u.x - targetV.x;
        double dy = u.y - targetV.y;
        return Math.sqrt(dx * dx + dy * dy) / 200.0;
    }

    private void reconstructPath(Map<Integer, Integer> parent) {
        LinkedList<Integer> path = new LinkedList<>();
        int curr = target;
        while (parent.containsKey(curr) || curr == start) {
            path.addFirst(curr);
            if (curr == start) break;
            curr = parent.get(curr);
        }

        int prev = -1;
        for (int u : path) {
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, u, -1));
            if (prev != -1) {
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, prev, u));
            }
            prev = u;
        }
    }

    private static class NodeCost {
        int id;
        double cost;

        public NodeCost(int id, double cost) {
            this.id = id;
            this.cost = cost;
        }
    }
}