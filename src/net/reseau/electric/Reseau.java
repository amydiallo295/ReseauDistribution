package net.reseau.electric;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe principale représentant un réseau de distribution électrique.
 * Gère les générateurs, les maisons et les connexions entre eux.
 * Permet de valider, calculer le coût et optimiser la distribution électrique.
 * 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public class Reseau {
    /** Map associant le nom d'un générateur à son objet Generateur */
    private Map<String, Generateur> generateurs = new HashMap<>();
    
    /** Map associant le nom d'une maison à son objet Maison */
    private Map<String, Maison> maisons = new HashMap<>();
    
    /** Map associant le nom d'une maison au nom du générateur auquel elle est connectée */
    private Map<String, String> connexions = new HashMap<>(); // maison -> générateur
    
    /** Mode silencieux : désactive les messages System.out lors de l'optimisation */
    private boolean modeSilencieux = false;

    /**
     * Ajoute un générateur au réseau ou met à jour sa capacité s'il existe déjà.
     * 
     * @param nom le nom unique du générateur
     * @param capacite la capacité maximale en kW
     */
    public void ajouterGenerateur(String nom, int capacite) {
        if (generateurs.containsKey(nom)) {
            System.out.println("MAJ: Capacité du générateur " + nom + " mise à jour.");
        }
        generateurs.put(nom, new Generateur(nom, capacite));
    }

    /**
     * Ajoute une maison au réseau ou met à jour son type si elle existe déjà.
     * 
     * @param nom le nom unique de la maison
     * @param typeStr le type de maison sous forme de chaîne ("BASSE", "NORMAL" ou "FORTE")
     */
    public void ajouterMaison(String nom, String typeStr) {
        try {
            TypeMaison type = TypeMaison.valueOf(typeStr.toUpperCase());
            if (maisons.containsKey(nom)) {
                System.out.println("MAJ: Consommation de la maison " + nom + " mise à jour.");
            }
            maisons.put(nom, new Maison(nom, type));
        } catch (IllegalArgumentException e) {
            System.out.println("Type inconnu pour la maison " + nom + " (" + typeStr + "). Utilisez BASSE, NORMAL ou FORTE.");
        }
    }

    /**
     * Ajoute une connexion entre une maison et un générateur.
     * Vérifie l'existence de la maison et du générateur.
     * Détecte et avertit en cas de surcharge du générateur.
     * Si la maison était déjà connectée, la connexion précédente est remplacée.
     * 
     * @param nomMaison le nom de la maison à connecter
     * @param nomGenerateur le nom du générateur cible
     */
    public void ajouterConnexion(String nomMaison, String nomGenerateur){
        /* Méthode permettant d'ajouter des connexions dans la liste des connexions */
        
        // Vérification de l'existence de la maison
        if (!maisons.containsKey(nomMaison)) { 
            if (!modeSilencieux) System.out.println("Erreur : La maison " + nomMaison + " n'existe pas dans le réseau.");
            return;
        }
        Maison maison = maisons.get(nomMaison);

        // Vérification de l'existence du générateur
        if (!generateurs.containsKey(nomGenerateur)) { 
            if (!modeSilencieux) System.out.println("Erreur : Le générateur " + nomGenerateur + " n'existe pas dans le réseau.");
            return;
        }
        Generateur generateur = generateurs.get(nomGenerateur);

        // Verifier si la maison est deja connectee (unicite de la connexion)
        if (connexions.containsKey(nomMaison)) {
            String ancienGenerateur = connexions.get(nomMaison);
            if (!modeSilencieux) {
                System.out.println("Info : La maison " + nomMaison + " etait connectee a " + ancienGenerateur + 
                                 ". Nouvelle connexion vers " + nomGenerateur + ".");
            }
        }

        // Calcul de la charge actuelle du générateur
        int chargeActuelle = 0;
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            // Ne pas compter la maison si elle est déjà connectée à ce générateur
            if (entry.getValue().equals(nomGenerateur) && !entry.getKey().equals(nomMaison)) {
                chargeActuelle += maisons.get(entry.getKey()).getDemande();
            }
        }

        // Vérifier si la connexion provoque une surcharge
        int nouvelleCharge = chargeActuelle + maison.getDemande();
        if (nouvelleCharge > generateur.getCapacite()) {
            if (!modeSilencieux) {
                System.out.println("[!] AVERTISSEMENT : connecter " + nomMaison + " a " + nomGenerateur +
                                 " provoque une surcharge (" + nouvelleCharge + "/" + 
                                 generateur.getCapacite() + " kW).");
                System.out.println("    Vous devez modifier le reseau pour eliminer la surcharge avant de calculer le cout.");
            }
        }
        
        // Ajout de la connexion (même en cas de surcharge)
        connexions.put(nomMaison, nomGenerateur);
        if (!modeSilencieux) System.out.println("Connexion ajoutee : " + nomMaison + " -> " + nomGenerateur);
    }

    /**
     * Supprime une connexion existante entre une maison et un générateur.
     * Vérifie que la maison, le générateur et la connexion existent.
     * 
     * @param nomMaison le nom de la maison
     * @param nomGenerateur le nom du générateur
     */
    public void supprimerConnexion(String nomMaison, String nomGenerateur) {
        // Méthode permettant de supprimer une connexion entre une maison et un générateur
    // Vérifier que la maison existe
    if (!maisons.containsKey(nomMaison)) {
        System.out.println("Erreur : la maison " + nomMaison + " n'existe pas dans le réseau.");
        return;
    }

    // Vérifier que le générateur existe
    if (!generateurs.containsKey(nomGenerateur)) {
        System.out.println("Erreur : le générateur " + nomGenerateur + " n'existe pas dans le réseau.");
        return;
    }

    // Vérifier que la connexion existe entre cette maison et ce générateur
    if (!connexions.containsKey(nomMaison) || !connexions.get(nomMaison).equals(nomGenerateur)) {
        System.out.println("Erreur : la connexion entre " + nomMaison + " et " + nomGenerateur + " n'existe pas.");
        return;
    }

    // Supprimer la connexion
    connexions.remove(nomMaison);
    System.out.println("Connexion supprimee : " + nomMaison + " -/-> " + nomGenerateur);
}

    /**
     * Supprime la connexion d'une maison sans vérifier le générateur cible.
     * Utilisé lors des modifications de connexions.
     * 
     * @param nomMaison le nom de la maison dont la connexion doit être supprimée
     */
    public void enleverConnexionMaison(String nomMaison) {
        connexions.remove(nomMaison);
    }
    
    /**
     * Active ou désactive le mode silencieux (sans messages console).
     * Utile pour l'optimisation dans l'interface graphique.
     * 
     * @param silencieux true pour activer, false pour désactiver
     */
    public void setModeSilencieux(boolean silencieux) {
        this.modeSilencieux = silencieux;
    }
    
    /**
     * Vérifie si le mode silencieux est activé.
     * 
     * @return true si le mode silencieux est actif
     */
    public boolean isModeSilencieux() {
        return modeSilencieux;
    }

    /**
     * Vérifie si une maison a une connexion active.
     * 
     * @param nomMaison le nom de la maison à vérifier
     * @return true si la maison est connectée, false sinon
     */
    public boolean connexionExiste(String nomMaison) {
        return connexions.containsKey(nomMaison);
    }


    /**
     * Affiche dans la console l'état complet du réseau :
     * - tous les générateurs avec leur capacité
     * - toutes les maisons avec leur type et demande
     * - toutes les connexions existantes
     */
    public void afficher() {
        System.out.println("\n--- Réseau actuel ---");
        System.out.println("Générateurs :");
        for (Generateur g : generateurs.values()) {
            System.out.println("  " + g.getNom() + " (" + g.getCapacite() + " kW)");
        }
        System.out.println("Maisons :");
        for (Maison m : maisons.values()) {
            System.out.println("  " + m.getNom() + " (" + m.getType() + ", " + m.getDemande() + " kW)");
        }
        System.out.println("Connexions :");
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("---------------------\n");
    }

    /**
     * Vérifie que toutes les maisons ont une connexion.
     * Affiche les maisons sans connexion le cas échéant.
     * 
     * @return true si toutes les maisons sont connectées, false sinon
     */
    public boolean verifierConnexion() {
        boolean ok = true;
        for (String m : maisons.keySet()) {
            if (!connexions.containsKey(m)) {
                System.out.println("Maison sans connexion : " + m);
                ok = false;
            }
        }
        return ok;
    }

    /**
     * Valide toutes les restrictions du réseau électrique
     */
    public boolean validerReseau() {
        boolean valide = true;
        
        // Restriction 1: Le réseau doit contenir toujours au moins une maison et un générateur
        if (maisons.isEmpty()) {
            System.out.println("ERREUR: Le réseau doit contenir au moins une maison.");
            valide = false;
        }
        
        if (generateurs.isEmpty()) {
            System.out.println("ERREUR: Le réseau doit contenir au moins un générateur.");
            valide = false;
        }
        
        // Si pas de maisons ou générateurs, pas besoin de continuer
        if (!valide) return false;
        
        // Restriction 2: Chaque maison doit être connectée à exactement un générateur
        for (String nomMaison : maisons.keySet()) {
            if (!connexions.containsKey(nomMaison)) {
                System.out.println("ERREUR: La maison " + nomMaison + " n'est connectée à aucun générateur.");
                valide = false;
            }
        }
        
        // Vérifier qu'il n'y a pas de connexions vers des générateurs inexistants
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            if (!generateurs.containsKey(entry.getValue())) {
                System.out.println("ERREUR: Connexion vers un générateur inexistant: " + entry.getValue());
                valide = false;
            }
            if (!maisons.containsKey(entry.getKey())) {
                System.out.println("ERREUR: Connexion depuis une maison inexistante: " + entry.getKey());
                valide = false;
            }
        }
        
        // Restriction 3: Somme des demandes ≤ Somme des capacités maximales
        int sommeDemandes = 0;
        for (Maison maison : maisons.values()) {
            sommeDemandes += maison.getDemande();
        }
        
        int sommeCapacites = 0;
        for (Generateur generateur : generateurs.values()) {
            sommeCapacites += generateur.getCapacite();
        }
        
        if (sommeDemandes > sommeCapacites) {
            System.out.println("ERREUR: La somme des demandes (" + sommeDemandes + 
            " kW) dépasse la somme des capacités disponibles (" + sommeCapacites + " kW).");
            System.out.println("Il est impossible de satisfaire toutes les demandes avec cette configuration.");
            valide = false;
        }
        
        if (valide) {
            System.out.println("Toutes les restrictions du réseau sont respectées.");
            System.out.println("  - Demande totale: " + sommeDemandes + " kW");
            System.out.println("  - Capacité totale: " + sommeCapacites + " kW");
        }
        
        return valide;
    }

    /**
     * Calcule et affiche le coût total du réseau électrique.
     * Le coût est calculé selon la formule : Dispersion + λ × Surcharge
     * où λ (lambda) = 10.
     * 
     * Vérifie d'abord que le réseau respecte toutes les restrictions avant le calcul.
     */
    public void calculerCout() {
        // Vérifier d'abord que le réseau respecte toutes les restrictions
        if (!validerReseau()) {
            System.out.println("\nImpossible de calculer le coût : le réseau ne respecte pas toutes les restrictions.");
            return;
        }
        
        System.out.println("\nCalcul du coût du réseau...");
        final int lambda = 10;
        Map<String, Integer> charge = new HashMap<>();
        for (String g : generateurs.keySet()) charge.put(g, 0);
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            String maison = entry.getKey();
            String generateur = entry.getValue();
            charge.put(generateur, charge.get(generateur) + maisons.get(maison).getDemande());
        }
        // Calcul des taux d'utilisation
        Map<String, Double> utilisation = new HashMap<>();
        double sommeUtilisation = 0.0;
        for (String g : generateurs.keySet()) {
            double u = (double) charge.get(g) / generateurs.get(g).getCapacite();
            utilisation.put(g, u);
            sommeUtilisation += u;
        }
        double utilisationMoyenne = sommeUtilisation / generateurs.size();
        // Dispersion
        double disp = 0.0;
        for (double u : utilisation.values()){
            disp += Math.abs(u - utilisationMoyenne);
        }
        // Surcharge
        double surcharge = 0.0;
        for (String g : generateurs.keySet()) {
            int depassement = charge.get(g) - generateurs.get(g).getCapacite();
            if (depassement > 0)
                surcharge += (double) depassement / generateurs.get(g).getCapacite();
        }
        double cout = disp + lambda * surcharge;
        System.out.printf("Dispersion (Disp) : %.3f\n", disp);
        System.out.printf("Surcharge : %.3f\n", surcharge);
        System.out.printf("Coût total : %.3f\n", cout);
    }

    /**
     * Vérifie si le réseau contient des générateurs en surcharge
     * @return true si au moins un générateur est en surcharge, false sinon
     */
    public boolean aSurcharge() {
        Map<String, Integer> charge = new HashMap<>();
        // Initialiser les charges à 0
        for (String g : generateurs.keySet()) {
            charge.put(g, 0);
        }
        // Calculer les charges actuelles
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            String maison = entry.getKey();
            String generateur = entry.getValue();
            if (maisons.containsKey(maison)) {
                charge.put(generateur, charge.get(generateur) + maisons.get(maison).getDemande());
            }
        }
        // Vérifier les surcharges
        for (String g : generateurs.keySet()) {
            if (charge.get(g) > generateurs.get(g).getCapacite()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Affiche dans la console l'état détaillé des connexions :
     * - maisons connectées avec leur générateur
     * - maisons non connectées
     * - générateurs disponibles
     */
    public void afficherEtatConnexions() {
        System.out.println("\n--- État des connexions ---");
        
        System.out.println("Maisons connectées :");
        for (String maison : connexions.keySet()) {
            System.out.println("  " + maison + " -> " + connexions.get(maison));
        }
        
        System.out.println("Maisons non connectées :");
        for (String maison : maisons.keySet()) {
            if (!connexions.containsKey(maison)) {
                System.out.println("  " + maison);
            }
        }
        
        System.out.println("Générateurs disponibles :");
        for (String generateur : generateurs.keySet()) {
            System.out.println("  " + generateur);
        }
        System.out.println("---------------------------");
    }

    /**
     * Affiche dans la console toutes les connexions existantes.
     * Si aucune connexion n'existe, affiche un message approprié.
     */
    public void afficherConnexionsExistantes() {
        System.out.println("\n--- Connexions existantes ---");
        if (connexions.isEmpty()) {
            System.out.println("Aucune connexion existante.");
        } else {
            for (Map.Entry<String, String> entry : connexions.entrySet()) {
                System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
            }
        }
        System.out.println("-----------------------------");
    }

    /**
     * Retourne une représentation textuelle complète du réseau.
     * Inclut tous les générateurs, maisons et connexions.
     * 
     * @return une chaîne formatée décrivant le réseau
     */
    public String getReseauAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Reseau actuel ---\n");
        sb.append("Generateurs :\n");
        for (Generateur g : generateurs.values()) {
            sb.append("  ").append(g.getNom()).append(" (").append(g.getCapacite()).append(" kW)\n");
        }
        sb.append("Maisons :\n");
        for (Maison m : maisons.values()) {
            sb.append("  ").append(m.getNom()).append(" (").append(m.getType()).append(", ").append(m.getDemande()).append(" kW)\n");
        }
        sb.append("Connexions :\n");
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        sb.append("---------------------\n");
        return sb.toString();
    }

    /**
     * Retourne une représentation textuelle de toutes les connexions existantes.
     * 
     * @return une chaîne formatée décrivant les connexions
     */
    public String getConnexionsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Connexions existantes ---\n");
        if (connexions.isEmpty()) {
            sb.append("Aucune connexion existante.\n");
        } else {
            for (Map.Entry<String, String> entry : connexions.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            }
        }
        sb.append("-----------------------------\n");
        return sb.toString();
    }

    /**
     * Retourne une représentation textuelle de l'état des connexions.
     * Inclut les maisons connectées, non connectées et les générateurs disponibles.
     * 
     * @return une chaîne formatée décrivant l'état des connexions
     */
    public String getEtatConnexionsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Etat des connexions ---\n");
        
        sb.append("Maisons connectees :\n");
        for (String maison : connexions.keySet()) {
            sb.append("  ").append(maison).append(" -> ").append(connexions.get(maison)).append("\n");
        }
        
        sb.append("Maisons non connectees :\n");
        for (String maison : maisons.keySet()) {
            if (!connexions.containsKey(maison)) {
                sb.append("  ").append(maison).append("\n");
            }
        }
        
        sb.append("Generateurs disponibles :\n");
        for (String generateur : generateurs.keySet()) {
            sb.append("  ").append(generateur).append("\n");
        }
        sb.append("---------------------------\n");
        return sb.toString();
    }

    /**
     * Retourne la map de tous les générateurs du réseau.
     * 
     * @return map des générateurs (nom → Generateur)
     */
    public Map<String, Generateur> getGenerateurs() {
        return generateurs;
    }

    /**
     * Retourne la map de toutes les maisons du réseau.
     * 
     * @return map des maisons (nom → Maison)
     */
    public Map<String, Maison> getMaisons() {
        return maisons;
    }

    /**
     * Retourne la map de toutes les connexions du réseau.
     * 
     * @return map des connexions (nomMaison → nomGenerateur)
     */
    public Map<String, String> getConnexions() {
        return connexions;
    }

    /**
     * Calcule les charges actuelles de chaque générateur
     * @return Map associant chaque générateur à sa charge actuelle
     */
    public Map<String, Integer> getCharges() {
        Map<String, Integer> charges = new HashMap<>();
        for (String g : generateurs.keySet()) {
            charges.put(g, 0);
        }
        for (Map.Entry<String, String> entry : connexions.entrySet()) {
            String maison = entry.getKey();
            String generateur = entry.getValue();
            charges.put(generateur, charges.get(generateur) + maisons.get(maison).getDemande());
        }
        return charges;
    }

    /**
     * Calcule le coût total du réseau selon la formule : Disp + lambda * Surcharge
     * @param lambda coefficient de pénalisation de la surcharge
     * @return le coût total
     */
    public double calculerCoutTotal(int lambda) {
        Map<String, Integer> charge = getCharges();
        
        // Calcul des taux d'utilisation
        Map<String, Double> utilisation = new HashMap<>();
        double sommeUtilisation = 0.0;
        for (String g : generateurs.keySet()) {
            double u = (double) charge.get(g) / generateurs.get(g).getCapacite();
            utilisation.put(g, u);
            sommeUtilisation += u;
        }
        double utilisationMoyenne = sommeUtilisation / generateurs.size();
        
        // Dispersion
        double disp = 0.0;
        for (double u : utilisation.values()) {
            disp += Math.abs(u - utilisationMoyenne);
        }
        
        // Surcharge
        double surcharge = 0.0;
        for (String g : generateurs.keySet()) {
            int depassement = charge.get(g) - generateurs.get(g).getCapacite();
            if (depassement > 0)
                surcharge += (double) depassement / generateurs.get(g).getCapacite();
        }
        
        return disp + lambda * surcharge;
    }
}