package ui.layout;

import model.Vertex;
import java.util.Collection;

public class CircleLayout implements GraphLayoutStrategy {
    @Override
    public void arrange(Collection<Vertex> vertices) {
        int n = vertices.size();
        if (n == 0) return;
        double cx = 400, cy = 300, r = 200;
        int i = 0;
        for (Vertex v : vertices) {
            double angle = 2 * Math.PI * i / n;
            v.x = cx + r * Math.cos(angle);
            v.y = cy + r * Math.sin(angle);
            i++;
        }
    }
}