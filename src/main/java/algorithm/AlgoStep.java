package algorithm;

public class AlgoStep {

    public enum Type {
        VISIT_VERTEX,
        EXPLORE_EDGE,
        FINISH_VERTEX,
        HIGHLIGHT_PATH,
        HIGHLIGHT_NODE,
        FOUND_SCC
    }

    public final Type type;
    public final int u;
    public final int v;

    public AlgoStep(Type type, int u, int v) {
        this.type = type;
        this.u = u;
        this.v = v;
    }
}