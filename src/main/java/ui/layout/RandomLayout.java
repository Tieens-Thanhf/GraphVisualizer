package ui.layout;

import model.Vertex;
import java.util.Collection;
import java.util.Random;

public class RandomLayout implements GraphLayoutStrategy {
    @Override
    public void arrange(Collection<Vertex> vertices) {
        Random rand = new Random();
        double w = 800, h = 600, pad = 50;
        for (Vertex v : vertices) {
            v.x = pad + rand.nextDouble() * (w - 2 * pad);
            v.y = pad + rand.nextDouble() * (h - 2 * pad);
        }
    }
}