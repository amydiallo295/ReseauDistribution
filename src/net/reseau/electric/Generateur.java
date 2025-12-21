package net.reseau.electric;

/**
 * Représente un générateur d'électricité dans le réseau de distribution.
 * Un générateur possède un nom unique et une capacité maximale de production en kilowatts (kW).
 * 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public class Generateur {
    /** Nom unique du générateur */
    private final String nom;
    
    /** Capacité maximale de production en kilowatts (kW) */
    private final int capacite;

    /**
     * Construit un nouveau générateur avec un nom et une capacité.
     * 
     * @param nom le nom unique du générateur
     * @param capacite la capacité maximale de production en kW
     */
    public Generateur(String nom, int capacite){
        this.nom = nom;
        this.capacite = capacite;
    }

    /**
     * Retourne la capacité maximale du générateur.
     * 
     * @return la capacité en kW
     */
    public int getCapacite(){
        return capacite;

    }

    /**
     * Retourne le nom du générateur.
     * 
     * @return le nom du générateur
     */
    public String getNom(){
        return nom;
    }
}
    
