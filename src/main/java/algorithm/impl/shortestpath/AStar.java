package algorithm.impl.shortestpath;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.*;

public class AStar implements GraphAlgorithm {

    private final Graph graph;
    private final int start;
    private final int target;
    private final List<AlgoStep> steps;

    public AStar(Graph graph, int start, int target) {
        this.graph = graph;
        this.start = start;
        this.target = target;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        int n = graph.vertexCount();

        // gScore: Chi phí thực tế từ Start -> u
        Map<Integer, Double> gScore = new HashMap<>();

        // PQ sắp xếp theo f(n) = g(n) + h(n)
        PriorityQueue<NodeCost> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fCost));

        Map<Integer, Integer> parent = new HashMap<>();

        // Init
        gScore.put(start, 0.0);
        Vertex targetV = graph.getVertex(target);
        pq.add(new NodeCost(start, heuristic(start, targetV)));

        boolean found = false;

        while (!pq.isEmpty()) {
            NodeCost current = pq.poll();
            int u = current.id;

            // Nếu lấy ra đỉnh mà đã có đường ngắn hơn trong gScore rồi thì bỏ qua (Lazy deletion)
            // (Lưu ý: Logic A* chuẩn cần update priority, nhưng trong Java PQ ko hỗ trợ update
            // nên ta cứ add thêm vào, và check ở đây)
            if (current.fCost > gScore.getOrDefault(u, Double.MAX_VALUE) + heuristic(u, targetV)) {
                // Check hơi phức tạp tí vì fCost bao gồm cả h(n), nhưng đơn giản là:
                // Nếu gScore thực tế hiện tại còn tốt hơn cái node trong PQ thì node này là rác cũ.
                // Tuy nhiên để đơn giản, cứ process, A* vẫn đúng.
            }

            steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, -1));

            if (u == target) {
                found = true;
                break;
            }

            for (Edge e : graph.getAdj(u)) {
                int v = e.to;
                double newG = gScore.get(u) + e.weight;

                if (newG < gScore.getOrDefault(v, Double.MAX_VALUE)) {
                    gScore.put(v, newG);
                    parent.put(v, u);

                    double h = heuristic(v, targetV);
                    double f = newG + h;

                    pq.add(new NodeCost(v, f));

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
        double pixelDistance = Math.sqrt(dx * dx + dy * dy);

        return pixelDistance / 200.0;
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
        double fCost;

        public NodeCost(int id, double fCost) {
            this.id = id;
            this.fCost = fCost;
        }
    }
}