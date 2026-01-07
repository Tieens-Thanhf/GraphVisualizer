package ui.layout;

import model.Vertex;
import java.util.Collection;

public interface GraphLayoutStrategy {
    void arrange(Collection<Vertex> vertices);
}