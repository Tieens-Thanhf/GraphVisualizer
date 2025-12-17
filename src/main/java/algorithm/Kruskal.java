package algorithm;

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

        // 1. Lấy tất cả các cạnh ra và sắp xếp tăng dần theo trọng số
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(Comparator.comparingInt(e -> e.weight));

        // 2. Khởi tạo DSU
        DSU dsu = new DSU(n);

        int edgesCount = 0;

        for (Edge e : edges) {
            // Hiệu ứng: Đang xem xét cạnh này
            steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, e.from, e.to));

            // Nếu u và v chưa cùng thuộc một cây (không tạo chu trình)
            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);

                // Hiệu ứng: CHẤP NHẬN cạnh (Tô màu xanh lá/đậm)
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, e.from, e.to));
                // Highlight cả 2 đỉnh để báo hiệu chúng đã thuộc về cây khung
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, e.from, -1));
                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, e.to, -1));

                edgesCount++;
            }
            // Nếu muốn visual kỹ hơn: Có thể thêm bước tô màu ĐỎ nếu cạnh bị loại bỏ

            // Cây khung chỉ cần n-1 cạnh là đủ
            if (edgesCount == n - 1) break;
        }

        return steps;
    }

    // === Helper Class: Disjoint Set Union (DSU) ===
    private static class DSU {
        private final int[] parent;

        public DSU(int n) {
            parent = new int[n + 1];
            for (int i = 1; i <= n; i++) parent[i] = i;
        }

        public int find(int u) {
            if (parent[u] == u) return u;
            return parent[u] = find(parent[u]); // Path compression
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