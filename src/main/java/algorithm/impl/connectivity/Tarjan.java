package algorithm.impl.connectivity;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import java.util.*;

public class Tarjan implements GraphAlgorithm {

    private final Graph graph;
    private final List<AlgoStep> steps;

    // Các biến phục vụ thuật toán
    private int idCounter;
    private int sccCount;
    private int n;

    private int[] ids; // id[u]: thời điểm thăm u
    private int[] low; // low[u]: id nhỏ nhất u có thể chạm tới
    private boolean[] onStack; // Kiểm tra u có trong stack không
    private Deque<Integer> stack; // Stack dùng cho thuật toán

    public Tarjan(Graph graph) {
        this.graph = graph;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        n = graph.vertexCount();

        ids = new int[n + 1];
        low = new int[n + 1];
        onStack = new boolean[n + 1];
        stack = new ArrayDeque<>();

        Arrays.fill(ids, -1); // -1 nghĩa là chưa thăm
        idCounter = 0;
        sccCount = 0;

        // Tarjan cần chạy vòng lặp qua tất cả các đỉnh để đảm bảo đồ thị rời rạc cũng được duyệt hết
        for (int i = 1; i <= n; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }

        return steps;
    }

    private void dfs(int at) {
        stack.push(at);
        onStack[at] = true;
        ids[at] = low[at] = idCounter++;

        // Visual: Thăm đỉnh
        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, at, -1));

        List<Edge> neighbors = graph.getAdj(at);
        // Sort để duyệt nhất quán (Optional)
        neighbors.sort(Comparator.comparingInt(e -> e.to));

        for (Edge e : neighbors) {
            int to = e.to;

            // Visual: Xét cạnh
            steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, at, to));

            if (ids[to] == -1) {
                // Nếu chưa thăm -> DFS tiếp
                dfs(to);
                low[at] = Math.min(low[at], low[to]);
            } else if (onStack[to]) {
                // Nếu đã thăm và đang trong stack -> Cập nhật low (đây là back-edge)
                low[at] = Math.min(low[at], ids[to]);
            }
        }

        // Sau khi thăm hết con, kiểm tra xem 'at' có phải là chốt của một SCC không
        if (ids[at] == low[at]) {
            // Đã tìm thấy một SCC mới!
            while (!stack.isEmpty()) {
                int node = stack.pop();
                onStack[node] = false;

                // Visual: Đỉnh 'node' thuộc về nhóm SCC thứ 'sccCount'
                // Ta truyền sccCount vào tham số thứ 2 (v) của AlgoStep
                steps.add(new AlgoStep(AlgoStep.Type.FOUND_SCC, node, sccCount));

                if (node == at) break;
            }
            sccCount++;
        }
    }
}