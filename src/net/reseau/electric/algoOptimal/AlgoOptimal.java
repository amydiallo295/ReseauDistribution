package net.reseau.electric.algoOptimal;

import java.util.*;
import net.reseau.electric.Reseau;

/**
 * Classe implémentant des algorithmes d'optimisation pour les réseaux électriques.
 * Utilise l'algorithme GRASP (Greedy Randomized Adaptive Search Procedure)
 * qui combine construction greedy randomisée et recherche locale.
 * 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public class AlgoOptimal {

    /**
     * Algorithme GRASP (Greedy Randomized Adaptive Search Procedure).
     * Combine construction greedy randomisée avec recherche locale itérative.
     * 
     * L'algorithme fonctionne en deux phases :
     * 1. Construction greedy randomisée : construit une solution en choisissant
     *    semi-aléatoirement parmi les meilleurs choix
     * 2. Recherche locale : améliore la solution en testant des modifications
     * 
     * @param reseau le réseau à optimiser (modifié sur place)
     * @param lambda coefficient de pénalisation de la surcharge
     * @param maxIterations nombre d'itérations GRASP
     * @param alpha paramètre de randomisation (0 = greedy pur, 1 = aléatoire)
     */
    public static void resoudreOptimise(Reseau reseau, int lambda, int maxIterations, double alpha) {
        System.out.println("\n=== Algorithme GRASP (Greedy Randomized Adaptive Search) ===");
        System.out.printf("Parametres: iterations=%d, alpha=%.2f, lambda=%d\n", maxIterations, alpha, lambda);
        
        Random random = new Random();
        Map<String, String> meilleuresSolutions = new HashMap<>();
        double meilleurCout = Double.MAX_VALUE;
        
        for (int iter = 0; iter < maxIterations; iter++) {
            // Phase 1: Construction greedy randomisee
            construireSolutionGreedyRandomisee(reseau, lambda, alpha, random);
            
            // Phase 2: Recherche locale (amelioration)
            rechercheLocale(reseau, lambda, 50);
            
            // Evaluer la solution
            double coutActuel = reseau.calculerCoutTotal(lambda);
            
            // Garder la meilleure solution trouvee
            if (coutActuel < meilleurCout) {
                meilleurCout = coutActuel;
                meilleuresSolutions = new HashMap<>(reseau.getConnexions());
                System.out.printf("[Iter %d] Meilleur cout: %.6f\n", iter + 1, meilleurCout);
            }
        }
        
        // Restaurer la meilleure solution
        for (Map.Entry<String, String> entry : meilleuresSolutions.entrySet()) {
            reseau.ajouterConnexion(entry.getKey(), entry.getValue());
        }
        
        System.out.printf("\n>>> Cout final: %.6f\n", meilleurCout);
    }
    
    /**
     * Construit une solution en utilisant un algorithme greedy randomisé.
     * Pour chaque maison, calcule le coût d'ajout à chaque générateur,
     * puis choisit aléatoirement parmi les meilleurs candidats (RCL).
     * 
     * @param reseau le réseau à modifier
     * @param lambda coefficient de pénalisation
     * @param alpha paramètre de randomisation pour la RCL
     * @param random générateur de nombres aléatoires
     */
    private static void construireSolutionGreedyRandomisee(Reseau reseau, int lambda, double alpha, Random random) {
        // Supprimer toutes les connexions existantes
        List<String> maisons = new ArrayList<>(reseau.getMaisons().keySet());
        for (String maison : maisons) {
            reseau.enleverConnexionMaison(maison);
        }
        
        // Trier par demande décroissante
        maisons.sort((m1, m2) -> 
            Integer.compare(reseau.getMaisons().get(m2).getDemande(), 
                          reseau.getMaisons().get(m1).getDemande()));
        
        // Pour chaque maison, choisir un générateur de façon semi-aléatoire
        for (String maison : maisons) {
            List<String> generateurs = new ArrayList<>(reseau.getGenerateurs().keySet());
            
            // Calculer le coût pour chaque générateur
            Map<String, Double> couts = new HashMap<>();
            double minCout = Double.MAX_VALUE;
            double maxCout = Double.MIN_VALUE;
            
            for (String gen : generateurs) {
                reseau.ajouterConnexion(maison, gen);
                double cout = reseau.calculerCoutTotal(lambda);
                couts.put(gen, cout);
                minCout = Math.min(minCout, cout);
                maxCout = Math.max(maxCout, cout);
                reseau.enleverConnexionMaison(maison);
            }
            
            // Créer une liste restreinte de candidats (RCL)
            double seuil = minCout + alpha * (maxCout - minCout);
            List<String> rcl = new ArrayList<>();
            for (String gen : generateurs) {
                if (couts.get(gen) <= seuil) {
                    rcl.add(gen);
                }
            }
            
            // Choisir aléatoirement dans la RCL
            String genChoisi = rcl.get(random.nextInt(rcl.size()));
            reseau.ajouterConnexion(maison, genChoisi);
        }
    }
    
    /**
     * Recherche locale : explore le voisinage pour améliorer la solution.
     * Pour chaque maison, teste tous les générateurs possibles et garde
     * le changement qui améliore le coût.
     * S'arrête quand aucune amélioration n'est trouvée.
     * 
     * @param reseau le réseau à optimiser
     * @param lambda coefficient de pénalisation
     * @param maxIterLocale nombre maximum d'itérations de recherche locale
     */
    private static void rechercheLocale(Reseau reseau, int lambda, int maxIterLocale) {
        boolean amelioration = true;
        int iter = 0;
        
        while (amelioration && iter < maxIterLocale) {
            amelioration = false;
            double coutActuel = reseau.calculerCoutTotal(lambda);
            
            // Pour chaque maison, tester tous les générateurs
            for (String maison : reseau.getMaisons().keySet()) {
                String genActuel = reseau.getConnexions().get(maison);
                
                for (String nouveauGen : reseau.getGenerateurs().keySet()) {
                    if (nouveauGen.equals(genActuel)) continue;
                    
                    // Essayer le changement
                    reseau.ajouterConnexion(maison, nouveauGen);
                    double nouveauCout = reseau.calculerCoutTotal(lambda);
                    
                    // Si amélioration, garder
                    if (nouveauCout < coutActuel) {
                        coutActuel = nouveauCout;
                        genActuel = nouveauGen;
                        amelioration = true;
                    } else {
                        // Sinon, annuler
                        reseau.ajouterConnexion(maison, genActuel);
                    }
                }
            }
            iter++;
        }
    }

}
