public class Generateur {
    private final String nom;
    private final int capacite;

    public Generateur(String nom, int capacite){
        this.nom = nom;
        this.capacite = capacite;
    }

    public int getCapacite(){
        return capacite;
    }

    public String getNom(){
        return nom;
    }
}