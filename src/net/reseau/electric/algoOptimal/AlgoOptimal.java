package net.reseau.electric.algoOptimal;

import net.reseau.electric.Reseau;
import java.util.*;

public class AlgoOptimal {

    /**
     * Algorithme d'amélioration locale optimisé utilisant les méthodes publiques de Reseau
     * @param reseau le réseau à optimiser (modifié sur place)
     * @param lambda la sévérité de la pénalisation
     * @param maxIter le nombre maximum d'itérations
     */
    public static void resoudreOptimise(Reseau reseau, int lambda, int maxIter) {
        boolean improved = true;
        int iter = 0;

        while (improved && iter < maxIter) {
            improved = false;

            // Charges actuelles et générateurs surchargés
            Map<String, Integer> charges = reseau.getCharges();
            List<String> surcharges = new ArrayList<>();
            for (String g : charges.keySet()) {
                if (charges.get(g) > reseau.getGenerateurs().get(g).getCapacite()) {
                    surcharges.add(g);
                }
            }

            double bestCout = reseau.calculerCoutTotal(lambda);
            String bestMaison = null, bestGen = null;

            // Boucle sur maisons connectées à des générateurs surchargés
            for (String gen : surcharges) {
                for (Map.Entry<String, String> entry : reseau.getConnexions().entrySet()) {
                    String maison = entry.getKey();
                    String oldGen = entry.getValue();

                    if (!oldGen.equals(gen)) continue; // Maison pas sur générateur surchargé

                    for (String cibleGen : reseau.getGenerateurs().keySet()) {
                        if (cibleGen.equals(oldGen)) continue;

                        // Vérifier qu'on ne surcharge pas le générateur cible
                        int nouvelleCharge = reseau.getCharges().get(cibleGen) + reseau.getMaisons().get(maison).getDemande();
                        if (nouvelleCharge > reseau.getGenerateurs().get(cibleGen).getCapacite()) continue;

                        // Tentative de déplacer la maison
                        reseau.ajouterConnexion(maison, cibleGen);
                        double c = reseau.calculerCoutTotal(lambda);

                        if (c < bestCout) {
                            bestCout = c;
                            bestMaison = maison;
                            bestGen = cibleGen;
                            improved = true;
                        }

                        // Revert
                        reseau.ajouterConnexion(maison, oldGen);
                    }
                }
            }

            // Appliquer le meilleur échange trouvé
            if (improved && bestMaison != null && bestGen != null) {
                reseau.ajouterConnexion(bestMaison, bestGen);
            }

            iter++;
        }

        System.out.printf("Coût final optimisé : %.9f\n", reseau.calculerCoutTotal(lambda));
    }

}
