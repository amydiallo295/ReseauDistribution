package net.reseau.electric.io;
import net.reseau.electric.Reseau;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ReseauImporter {
    public static Reseau importer(String chemin) throws IOException {
        Reseau reseau = new Reseau();
        List<String> lignes = Files.readAllLines(new File(chemin).toPath());
        int etape = 0, ligneNum = 0;
        Set<String> generateurs = new HashSet<>(), maisons = new HashSet<>();
        for (String l : lignes) {
            ligneNum++;
            l = l.trim();
            if (l.isEmpty() || l.startsWith("//")) continue;
            if (!l.endsWith(".")) throw new IOException("Ligne " + ligneNum + " : chaque ligne doit se terminer par un point.");
            String contenu = l.substring(0, l.length()-1).replaceAll("\\s+", "");
            if (contenu.toLowerCase().startsWith("generateur(")) {
                if (etape > 0) throw new IOException("Ligne " + ligneNum + " : générateurs d'abord.");
                String[] parametres = contenu.substring(11, contenu.length()-1).split(",");
                reseau.ajouterGenerateur(parametres[0], Integer.parseInt(parametres[1]));
                generateurs.add(parametres[0]);
            } else if (contenu.toLowerCase().startsWith("maison(")) {
                if (etape == 0) etape = 1;
                if (etape > 1) throw new IOException("Ligne " + ligneNum + " : maisons avant connexions.");
                String[] parametres = contenu.substring(7, contenu.length()-1).split(",");
                reseau.ajouterMaison(parametres[0], parametres[1].toUpperCase());
                maisons.add(parametres[0]);
            } else if (contenu.toLowerCase().startsWith("connexion(")) {
                etape = 2;
                String[] parametres = contenu.substring(10, contenu.length()-1).split(",");
                String nomMaison = maisons.contains(parametres[0]) ? parametres[0] : parametres[1];
                String nomGenerateur = generateurs.contains(parametres[0]) ? parametres[0] : parametres[1];
                if (!maisons.contains(nomMaison) || !generateurs.contains(nomGenerateur))
                    throw new IOException("Ligne " + ligneNum + " : connexion entre éléments non définis.");
                reseau.ajouterConnexion(nomMaison, nomGenerateur);
            } else {
                throw new IOException("Ligne " + ligneNum + " : élément non reconnu (" + l + ")");
            }
        }
        return reseau;
    }
}
