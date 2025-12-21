package net.reseau.electric;

/**
 * Représente une maison consommatrice d'électricité dans le réseau de distribution.
 * Chaque maison possède un nom unique et un type qui détermine sa consommation énergétique.
 * 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public class Maison {
    /** Nom unique de la maison */
    private String nom;
    
    /** Type de maison définissant sa consommation */
    private TypeMaison type;

    /**
     * Construit une nouvelle maison avec un nom et un type.
     * 
     * @param nom le nom unique de la maison
     * @param type le type de maison (BASSE, NORMAL ou FORTE)
     */
    public Maison(String nom, TypeMaison type){
        this.nom = nom;
        this.type = type;
    }

    /**
     * Retourne la demande énergétique de la maison en kilowatts.
     * La demande dépend du type de maison.
     * 
     * @return la demande en kW
     */
    public int getDemande(){
        return type.demande;
    }
    
    /**
     * Retourne le nom de la maison.
     * 
     * @return le nom de la maison
     */
    public String getNom(){
        return nom;
    }

    /**
     * Retourne le type de la maison.
     * 
     * @return le type de maison
     */
    public TypeMaison getType(){
        return type;
    }
}
