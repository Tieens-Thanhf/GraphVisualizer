package algorithm;

import algorithm.impl.connectivity.Tarjan;
import algorithm.impl.flow.MaxFlow;
import algorithm.impl.shortestpath.AStar;
import algorithm.impl.shortestpath.Dijkstra;
import algorithm.impl.shortestpath.GBFS;
import algorithm.impl.traversal.BFS;
import algorithm.impl.traversal.DFS;
import algorithm.impl.tree.Kruskal;
import model.Graph;

public class AlgoFactory {

    public static GraphAlgorithm createAlgorithm(AlgorithmType type, Graph graph, int start, int target) {
        switch (type) {
            case BFS:
                // BFS cũng có thể tìm đường ngắn nhất (trên đồ thị không trọng số)
                // Bạn có thể update BFS tương tự Dijkstra để nhận target
                return new BFS(graph, start);
            case DFS:
                return new DFS(graph, start);
            case DIJKSTRA:
                return new Dijkstra(graph, start, target);// Truyền target vào
            case KRUSKAL:
                return new Kruskal(graph);
            case TARJAN:
                return new Tarjan(graph);
            case MAX_FLOW:
                return new MaxFlow(graph, start, target);
            case GBFS:
                return new GBFS(graph, start, target);
            case ASTAR:
                return new AStar(graph, start, target);
            default:
                throw new IllegalArgumentException("Chưa hỗ trợ");
        }
    }
}