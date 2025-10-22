import java.util.Map;
import java.util.HashMap;

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
        if(! maisons.containsKey(nomMaison) || ! generateurs.containsKey(nomGenerateur))
            throw new IllegalArgumentException("Erreur de type ! Veuillez choisir entre : BASSE, NORMAL, HAUTE");
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