package algorithm;

public class AlgoStep {

    public enum Type {
        VISIT_VERTEX,
        EXPLORE_EDGE,
        FINISH_VERTEX,
        HIGHLIGHT_PATH,
        HIGHLIGHT_NODE,
        FOUND_SCC,
        UPDATE_FLOW_TEXT,
        SHOW_RESULT
    }

    public final Type type;
    public final int u;
    public final int v;
    public final String extraData; // <--- MỚI: Chứa text (VD: "5/10")

    // Constructor cũ (giữ lại để không lỗi code cũ)
    public AlgoStep(Type type, int u, int v) {
        this(type, u, v, null);
    }

    // Constructor mới đầy đủ
    public AlgoStep(Type type, int u, int v, String extraData) {
        this.type = type;
        this.u = u;
        this.v = v;
        this.extraData = extraData;
    }
}