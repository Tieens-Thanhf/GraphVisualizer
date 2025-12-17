package algorithm;

import model.Edge;
import model.Graph;
import java.util.*;

public class Dijkstra implements GraphAlgorithm {

    private final Graph graph;
    private final int startNode;
    private final int targetNode; // <--- MỚI
    private final List<AlgoStep> steps;

    // Constructor nhận thêm targetNode (-1 nếu không muốn tìm đích cụ thể)
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

        int[] parent = new int[n + 1]; // Mảng truy vết
        Arrays.fill(parent, -1);

        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.dist));
        pq.add(new NodeDist(startNode, 0));

        boolean[] finalized = new boolean[n + 1];
        boolean targetFound = false; // Cờ đánh dấu

        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, startNode, -1));

        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            int u = current.id;

            if (finalized[u]) continue;
            finalized[u] = true;

            if (u != startNode) {
                steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, parent[u]));
            }

            // Hiệu ứng thăm đỉnh
            steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, u, parent[u]));

            // === 1. KIỂM TRA ĐÍCH (EARLY EXIT) ===
            if (u == targetNode) {
                targetFound = true;
                break; // Dừng ngay vòng lặp, không cần duyệt tiếp các đỉnh thừa
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

        // === 2. TRUY VẾT ĐƯỜNG ĐI (BACKTRACKING) ===
        if (targetFound) {
            reconstructPath(parent, targetNode);
        }

        return steps;
    }

    // Hàm lần ngược từ Đích -> Nguồn để tạo hiệu ứng tô màu đường đi
    private void reconstructPath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(path);

        // Tô màu Đỉnh bắt đầu
        if (!path.isEmpty()) {
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, path.get(0), -1));
        }

        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);

            // 1. Tô màu cạnh nối u -> v
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, u, v));

            // 2. Tô màu đỉnh đích v (để nó chuyển từ Xanh -> Đỏ)
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, v, -1));
        }
    }

    private static class NodeDist {
        int id, dist;
        public NodeDist(int id, int dist) { this.id = id; this.dist = dist; }
    }
}