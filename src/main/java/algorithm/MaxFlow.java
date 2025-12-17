package algorithm;

import model.Edge;
import model.Graph;
import java.util.*;

public class MaxFlow implements GraphAlgorithm {

    private final Graph graph;
    private final int s;
    private final int t;
    private final List<AlgoStep> steps;

    private int[][] capacity;
    private int[][] flow;
    private List<Integer>[] adj;
    private int[] parent;

    public MaxFlow(Graph graph, int s, int t) {
        this.graph = graph;
        this.s = s;
        this.t = t;
        this.steps = new ArrayList<>();
    }

    @Override
    public List<AlgoStep> run() {
        steps.clear();
        int n = graph.vertexCount();

        capacity = new int[n + 1][n + 1];
        flow = new int[n + 1][n + 1];
        adj = new ArrayList[n + 1];
        parent = new int[n + 1];

        for (int i = 1; i <= n; i++) adj[i] = new ArrayList<>();

        int initialTotalFlow = 0;

        for (Edge e : graph.getEdges()) {
            capacity[e.from][e.to] = e.weight;

            int validFlow = Math.min(e.currentFlow, e.weight);
            flow[e.from][e.to] = validFlow;

            adj[e.from].add(e.to);
            adj[e.to].add(e.from);

            steps.add(new AlgoStep(AlgoStep.Type.UPDATE_FLOW_TEXT, e.from, e.to,
                    validFlow + " / " + e.weight));

            if (e.from == s) {
                initialTotalFlow += validFlow;
            }
        }

        int maxFlow = initialTotalFlow;

        while (bfs(n)) {
            int pathFlow = Integer.MAX_VALUE;
            int v = t;
            while (v != s) {
                int u = parent[v];

                int residual = 0;
                if (capacity[u][v] > 0) {
                    residual = capacity[u][v] - flow[u][v];
                } else {
                    residual = flow[v][u];
                }

                pathFlow = Math.min(pathFlow, residual);
                v = u;
            }

            v = t;
            while (v != s) {
                int u = parent[v];

                if (capacity[u][v] > 0) {
                    flow[u][v] += pathFlow;
                } else {
                    flow[v][u] -= pathFlow;
                }

                steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_PATH, u, v));

                if (graphContainsEdge(u, v)) {
                    String status = flow[u][v] + " / " + capacity[u][v];
                    steps.add(new AlgoStep(AlgoStep.Type.UPDATE_FLOW_TEXT, u, v, status));
                } else if (graphContainsEdge(v, u)) {
                    String status = flow[v][u] + " / " + capacity[v][u];
                    steps.add(new AlgoStep(AlgoStep.Type.UPDATE_FLOW_TEXT, v, u, status));
                }

                v = u;
            }
            maxFlow += pathFlow;
            steps.add(new AlgoStep(AlgoStep.Type.HIGHLIGHT_NODE, t, -1));
        }

        for (Edge e : graph.getEdges()) {
            e.currentFlow = flow[e.from][e.to];
        }

        steps.add(new AlgoStep(AlgoStep.Type.SHOW_RESULT, -1, -1,
                "Tổng luồng cực đại: " + maxFlow));

        return steps;
    }

    private boolean bfs(int n) {
        Arrays.fill(parent, 0);
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        parent[s] = -1;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : adj[u]) {
                if (parent[v] == 0) {
                    int residual = 0;
                    if (capacity[u][v] > 0) residual = capacity[u][v] - flow[u][v];
                    else residual = flow[v][u]; // Cạnh ngược

                    if (residual > 0) {
                        parent[v] = u;
                        if (v == t) return true;
                        q.add(v);
                    }
                }
            }
        }
        return false;
    }

    private boolean graphContainsEdge(int u, int v) {
        return capacity[u][v] > 0;
    }
}