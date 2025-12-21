package net.reseau.electric.io;
import java.io.*;
import net.reseau.electric.Reseau;

/**
 * Classe utilitaire pour exporter un réseau électrique vers un fichier texte. 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public class ReseauExporter {
    
    /**
     * Exporte un réseau électrique dans un fichier texte au format Prolog.
     * Le fichier contient trois types de prédicats :
     * - generateur(nom, capacité)
     * - maison(nom, type)
     * - connexion(maison, générateur)
     * 
     * @param reseau le réseau à exporter
     * @param chemin le chemin du fichier de destination
     * @throws IOException si une erreur d'écriture se produit
     */
    public static void exporter(Reseau reseau, String chemin) throws IOException {
        try (PrintWriter w = new PrintWriter(chemin)) {
            reseau.getGenerateurs().forEach((n, g) -> w.printf("generateur(%s,%d).\n", n, g.getCapacite()));
            reseau.getMaisons().forEach((n, m) -> w.printf("maison(%s,%s).\n", n, m.getType().name()));
            reseau.getConnexions().forEach((m, g) -> w.printf("connexion(%s,%s).\n", m, g));
        }
    }
}
