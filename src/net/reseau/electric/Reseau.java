import java.util.HashMap;
import java.util.Map;

public class Reseau{
    private Map<String, Generateur> generateurs = new HashMap<>();
    private Map<String, Maison> maisons = new HashMap<>();
    private Map<String, String> connections = new HashMap<>();

    public void ajouterGenerateur(String nomGenerateur, int capacite){
    /* Méthode permettant d'ajouter des générateurs dans la liste des générateurs */
        if(generateurs.containsKey(nomGenerateur)){
            System.out.println("MAJ : Générateur " + nomGenerateur + " mise à jour");
        }
        generateurs.put(nomGenerateur, new Generateur(nomGenerateur, capacite));
    }

    public void ajouterMaison(String nomMaison, TypeMaison type){
    /* Méthode permettant d'ajouter des maisons dans la liste des maisons  */
        if(maisons.containsKey(nomMaison)){
            System.out.println("MAJ : Maison " + nomMaison + " mise à jour");
        }
        maisons.put(nomMaison, new Maison(nomMaison, type));
    }

    public void ajouterConnections(String nomMaison, String nomGenerateur){
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

    // Vérification de la surcharge
    if (chargeActuelle + maison.getDemande() > generateur.getCapacite()) {
        // Chercher un autre générateur avec capacité disponible
        Generateur meilleurGen = null;
        int capaciteRestanteMax = -1;
        for (Generateur g : generateurs.values()) {
            int chargeG = 0;
            for (Map.Entry<String, String> entry : connections.entrySet()) {
                if (entry.getValue().equals(g.getNom())) {
                    chargeG += maisons.get(entry.getKey()).getDemande();
                }
            }
            int capaciteRestante = g.getCapacite() - chargeG;
            if (capaciteRestante >= maison.getDemande() && capaciteRestante > capaciteRestanteMax) {
                meilleurGen = g;
                capaciteRestanteMax = capaciteRestante;
            }
        }

        if (meilleurGen != null) {
            System.out.println("La maison " + nomMaison + " est connectée au générateur " + meilleurGen.getNom() + " pour éviter la surcharge de " + nomGenerateur);
            connections.put(nomMaison, meilleurGen.getNom());
            return;
        } else {
            System.out.println("Attention : aucun générateur ne peut accueillir " + nomMaison + " sans surcharge. Connexion quand même au générateur demandé.");
        }
    }
        //ajout de la connection
        connections.put(nomMaison, nomGenerateur);
       
    }

    public void enleverConnection(String nomMaison){
    /* Méthode permettant de supprimer une connection */
        connections.remove(nomMaison);
    }

    public void Afiichage(){
    /* Méthode permettant d'afficher le réseau actuel */
        System.out.println("\n--- Réseau actuel ---");
        System.out.println("Générateurs : ");
        for(Generateur g : generateurs.values()){
            System.out.println(" " + g.getNom() + " (" + g.getCapacite() + " kW)");
        }

        System.out.println("Maisons : ");
        for(Maison m : maisons.values()){
            System.out.println(" " + m.getNom() + " (" + m.getDemande() + " kW");
        }

        System.out.println("Connections : ");
        for(Map.Entry<String, String> entree : connections.entrySet()){
            System.out.println(" " + entree.getKey() + " -> " + entree.getValue());
        }

        System.out.println("----------------------\n");
    }
}