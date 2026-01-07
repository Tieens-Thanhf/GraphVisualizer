package algorithm.impl.connectivity;

import algorithm.AlgoStep;
import algorithm.GraphAlgorithm;
import model.Edge;
import model.Graph;
import java.util.*;

public class Tarjan implements GraphAlgorithm {

    private final Graph graph;
    private final List<AlgoStep> steps;

    private int idCounter;
    private int sccCount;
    private int n;

    private int[] ids;
    private int[] low;
    private boolean[] onStack;
    private Deque<Integer> stack;

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

        Arrays.fill(ids, -1);
        idCounter = 0;
        sccCount = 0;

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

        steps.add(new AlgoStep(AlgoStep.Type.VISIT_VERTEX, at, -1));

        List<Edge> neighbors = graph.getAdj(at);
        neighbors.sort(Comparator.comparingInt(e -> e.to));

        for (Edge e : neighbors) {
            int to = e.to;

            steps.add(new AlgoStep(AlgoStep.Type.EXPLORE_EDGE, at, to));

            if (ids[to] == -1) {
                dfs(to);
                low[at] = Math.min(low[at], low[to]);
            } else if (onStack[to]) {
                low[at] = Math.min(low[at], ids[to]);
            }
        }

        if (ids[at] == low[at]) {
            while (!stack.isEmpty()) {
                int node = stack.pop();
                onStack[node] = false;

                steps.add(new AlgoStep(AlgoStep.Type.FOUND_SCC, node, sccCount));

                if (node == at) break;
            }
            sccCount++;
        }
    }
}