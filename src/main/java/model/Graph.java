package model;

import java.util.*;

public class Graph {
    private Map<Integer, Vertex> vertexMap = new HashMap<>();
    private Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private List<Edge> allEdges = new ArrayList<>();

    private boolean directed = false;
    private boolean weighted = false;

    public Graph() {
    }

    public Graph(GraphConfig config) {
        this.directed = config.directed;
        this.weighted = config.weighted;
    }

    public int vertexCount() {
        return vertexMap.size();
    }

    public void createVertices(int n) {
        vertexMap.clear();
        adjacencyList.clear();
        allEdges.clear();

        double cx = 350;
        double cy = 250;
        double r = 150;

        for (int i = 1; i <= n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);

            Vertex v = new Vertex(i, x, y);
            vertexMap.put(i, v);
            adjacencyList.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        if (!vertexMap.containsKey(u) || !vertexMap.containsKey(v)) return;

        int w = isWeighted() ? weight : 1;

        for (Edge e : adjacencyList.get(u)) {
            if (e.to == v) {
                e.weight = w;

                if (!directed) {
                    updateReverseEdgeWeight(v, u, w);
                }
                return;
            }
        }

        Edge edgeUV = new Edge(u, v, w);
        adjacencyList.get(u).add(edgeUV);
        allEdges.add(edgeUV);

        if (!directed) {
            Edge edgeVU = new Edge(v, u, w);
            adjacencyList.get(v).add(edgeVU);
        }
    }

    private void updateReverseEdgeWeight(int u, int v, int w) {
        for (Edge e : adjacencyList.get(u)) {
            if (e.to == v) {
                e.weight = w;
                return;
            }
        }
    }

    public void clear() {
        vertexMap.clear();
        adjacencyList.clear();
        allEdges.clear();
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public boolean hasNegativeWeights() {
        if (!weighted) return false;

        for (Edge e : allEdges) {
            if (e.weight < 0) return true;
        }
        return false;
    }

    public List<Edge> getAdj(int u) {
        return adjacencyList.getOrDefault(u, Collections.emptyList());
    }

    public Vertex getVertex(int id) {
        return vertexMap.get(id);
    }

    public List<Vertex> getVertices() {
        return new ArrayList<>(vertexMap.values());
    }

    public List<Edge> getEdges() {
        return allEdges;
    }
}