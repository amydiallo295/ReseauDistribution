package net.reseau.electric;

public class Maison {
    private String nom;
    private TypeMaison type;

    public Maison(String nom, TypeMaison type){
        this.nom = nom;
        this.type = type;
    }

    public int getDemande(){
        return type.demande;
    }
    public String getNom(){
        return nom;
    }

    public TypeMaison getType(){
        return type;
    }
}
