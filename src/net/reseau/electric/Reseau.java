import java.util.HashMap;
import java.util.Map;

public class Reseau {
    private Map<String, Generateur> generateurs = new HashMap<>();
    private Map<String, Maison> maisons = new HashMap<>();
    private Map<String, String> connexions = new HashMap<>(); // maison -> générateur

    public void ajouterGenerateur(String nom, int capacite) {
        if (generateurs.containsKey(nom)) {
            System.out.println("MAJ: Capacité du générateur " + nom + " mise à jour.");
        }
        generateurs.put(nom, new Generateur(nom, capacite));
    }

    public void ajouterMaison(String nom, String typeStr) {
        try {
            TypeMaison type = TypeMaison.valueOf(typeStr.toUpperCase());
            if (maisons.containsKey(nom)) {
                System.out.println("MAJ: Consommation de la maison " + nom + " mise à jour.");
            }
            maisons.put(nom, new Maison(nom, type));
        } catch (IllegalArgumentException e) {
            System.out.println("Type inconnu. Utilisez BASSE, NORMALE ou FORTE.");
        }
    }

    public void ajouterConnection(String nomMaison, String nomGenerateur){
        /* Méthode permettant d'ajouter des connexions dans la liste des connexions */
        
        // Vérification de l'existence de la maison
        if (!maisons.containsKey(nomMaison)) { 
            System.out.println("Erreur : La maison " + nomMaison + " n'existe pas dans le réseau.");
            return;
        }
        Maison maison = maisons.get(nomMaison);

        // Vérification de l'existence du générateur
        if (!generateurs.containsKey(nomGenerateur)) { 
            System.out.println("Erreur : Le générateur " + nomGenerateur + " n'existe pas dans le réseau.");
            return;
        }
        Generateur generateur = generateurs.get(nomGenerateur);

        // Vérifier si la maison est déjà connectée (unicité de la connexion)
        if (connexions.containsKey(nomMaison)) {
            String ancienGenerateur = connexions.get(nomMaison);
            System.out.println("Info : La maison " + nomMaison + " était connectée à " + ancienGenerateur + 
                             ". Nouvelle connexion vers " + nomGenerateur + ".");
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
            System.out.println("Erreur : connecter " + nomMaison + " à " + nomGenerateur +
                             " provoquerait une surcharge (" + nouvelleCharge + "/" + 
                             generateur.getCapacite() + " kW). Connexion refusée.");
            return; // Empêche la connexion
        }
        
        // Ajout de la connexion (seulement si pas de surcharge)
        connexions.put(nomMaison, nomGenerateur);
        System.out.println("Connexion ajoutée : " + nomMaison + " -> " + nomGenerateur);
    }

    public void supprimerConnection(String nomMaison, String nomGenerateur) {
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
    System.out.println("Connexion supprimée : " + nomMaison + " -/-> " + nomGenerateur);
}


    public void enleverConnectionMaison(String nomMaison) {
        connexions.remove(nomMaison);
    }

    public boolean connectionExiste(String nomMaison) {
        return connexions.containsKey(nomMaison);
    }


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

    public boolean verifierConnection() {
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
        
        // Restriction 1: Le réseau contient toujours au moins une maison et un générateur
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
                             " kW) dépasse la somme des capacités disponibles (" + 
                             sommeCapacites + " kW).");
            System.out.println("Il est impossible de satisfaire toutes les demandes avec cette configuration.");
            valide = false;
        }
        
        if (valide) {
            System.out.println("✓ Toutes les restrictions du réseau sont respectées.");
            System.out.println("  - Demande totale: " + sommeDemandes + " kW");
            System.out.println("  - Capacité totale: " + sommeCapacites + " kW");
        }
        
        return valide;
    }

    public void calculerCout() {
        // Vérifier d'abord que le réseau respecte toutes les restrictions
        if (!validerReseau()) {
            System.out.println("\n❌ Impossible de calculer le coût : le réseau ne respecte pas toutes les restrictions.");
            return;
        }
        
        System.out.println("\n📊 Calcul du coût du réseau...");
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
}