import java.util.HashMap;
import java.util.Map;

public class Reseau {
    private Map<String, Generateur> generateurs = new HashMap<>();
    private Map<String, Maison> maisons = new HashMap<>();
    private Map<String, String> connections = new HashMap<>(); // maison -> générateur

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
    /* Méthode permettant d'ajouter des connections dans la liste des connections */
    //Verification de la maison
        if(! maisons.containsKey(nomMaison) ){ 
             throw new IllegalArgumentException("Erreur de type ! Veuillez choisir entre : BASSE, NORMAL, HAUTE");
        }
        Maison maison = maisons.get(nomMaison);

    //Verification si un generateur generateur précis est indiqué
        if(! generateurs.containsKey(nomGenerateur) ){ 
             throw new IllegalArgumentException("Erreur : Le générateur " + nomGenerateur + " n'existe pas dans le réseau.");
        }
        Generateur generateur = generateurs.get(nomGenerateur);

        /*calcul de la charge actuelle du générateur en parcourant toutes les maisons déja connecter a ce generateur et en affectuant 
        un somme de leurs demandes a fin de connaitre la charge actuelle*/

        int chargeActuelle = 0;
         for (Map.Entry<String, String> entry : connections.entrySet()) {
            if (entry.getValue().equals(nomGenerateur)) {
                chargeActuelle += maisons.get(entry.getKey()).getDemande();
            }
        }

   // Vérifier si la connexion provoque une surcharge
    if (chargeActuelle + maison.getDemande() > generateur.getCapacite()) {
        System.out.println("Attention : connecter " + nomMaison + " à " + nomGenerateur +
                           " risque de dépasser sa capacité (" + 
                           (chargeActuelle + maison.getDemande()) + "/" + 
                           generateur.getCapacite() + " kW).");
    }
        //ajout de la connection
        connections.put(nomMaison, nomGenerateur);

    }

    public void enleverConnection(String nomMaison) {
        connections.remove(nomMaison);
    }

    public boolean connectionExiste(String nomMaison) {
        return connections.containsKey(nomMaison);
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
        for (Map.Entry<String, String> entry : connections.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("---------------------\n");
    }

    public boolean verifierConnection() {
        boolean ok = true;
        for (String m : maisons.keySet()) {
            if (!connections.containsKey(m)) {
                System.out.println("Maison sans connexion : " + m);
                ok = false;
            }
        }
        return ok;
    }

    public void calculerCout() {
        final int lambda = 10;
        Map<String, Integer> charge = new HashMap<>();
        for (String g : generateurs.keySet()) charge.put(g, 0);
        for (Map.Entry<String, String> entry : connections.entrySet()) {
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
        for (double u : utilisation.values()) disp += Math.abs(u - utilisationMoyenne);
        // Surcharge
        double surcharge = 0.0;
        for (String g : generateurs.keySet()) {
            int depassement = charge.get(g) - generateurs.get(g).getCapacite();
            if (depassement > 0) surcharge += (double) depassement / generateurs.get(g).getCapacite();
        }
        double cout = disp + lambda * surcharge;
        System.out.printf("Dispersion (Disp) : %.3f\n", disp);
        System.out.printf("Surcharge : %.3f\n", surcharge);
        System.out.printf("Coût total : %.3f\n", cout);
    }
}