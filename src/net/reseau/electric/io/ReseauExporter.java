package net.reseau.electric.io;
import net.reseau.electric.Reseau;
import java.io.*;

public class ReseauExporter {
    public static void exporter(Reseau reseau, String chemin) throws IOException {
        try (PrintWriter w = new PrintWriter(chemin)) {
            reseau.getGenerateurs().forEach((n, g) -> w.printf("generateur(%s,%d).\n", n, g.getCapacite()));
            reseau.getMaisons().forEach((n, m) -> w.printf("maison(%s,%s).\n", n, m.getType().name()));
            reseau.getConnexions().forEach((m, g) -> w.printf("connexion(%s,%s).\n", m, g));
        }
    }
}
