package model;

public class Edge {
    public int from;
    public int to;
    public int weight;

    public int currentFlow = 0;

    public Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.currentFlow = 0;
    }
}