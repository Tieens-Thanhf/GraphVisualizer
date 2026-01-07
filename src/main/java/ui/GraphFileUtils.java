package ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;


public class GraphFileUtils {

    public static List<String> readLines(File file) throws IOException {
        return Files.readAllLines(file.toPath());
    }

    public static void saveGraphToFile(File file, String vertexCount, String edgeListText) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(vertexCount);
            writer.println(edgeListText);
        }
    }
}