import java.util.Map;
import java.util.HashMap;

public class Reseau{
    private Map<String, Generateur> generateurs = new HashMap<>();
    private Map<String, Maison> maisons = new HashMap<>();
    private Map<String, String> connections = new HashMap<>();

    public void ajouterGenerateur(String nomGenerateur, int capacite){
        if(generateurs.containsKey(nomGenerateur)){
            System.out.println("MAJ : Générateur " + nomGenerateur + " mise à jour");
        }
        generateurs.put(nomGenerateur, new Generateur(nomGenerateur, capacite));
    }

    public void ajouterMaison(String nomMaison, TypeMaison type){
        if(maisons.containsKey(nomMaison)){
            System.out.println("MAJ : Maison " + nomMaison + " mise à jour");
        }
        maisons.put(nomMaison, new Maison(nomMaison, type));
    }

    public void ajouterConnections(String nomMaison, String nomGenerateur){
        if(! maisons.containsKey(nomMaison) || ! generateurs.containsKey(nomGenerateur))
            throw new IllegalArgumentException("Erreur de type ! Veuillez choisir entre : BASSE, NORMAL, HAUTE");
        connections.put(nomMaison, nomGenerateur);
    }
}