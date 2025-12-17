package model;

public class GraphConfig {
    public boolean directed;
    public boolean weighted;

    public GraphConfig(boolean directed, boolean weighted) {
        this.directed = directed;
        this.weighted = weighted;
    }
}