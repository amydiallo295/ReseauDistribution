package net.reseau.electric;

/**
 * Énumération représentant les différents types de maisons selon leur consommation électrique.
 * Chaque type de maison a une demande en énergie fixe exprimée en kilowatts (kW).
 * 
 * @author Aminata Diallo, Elodie Cao
 * @version 1.0
 */
public enum TypeMaison {
    /** Maison à consommation basse (10 kW) */
    BASSE(10),
    
    /** Maison à consommation normale (20 kW) */
    NORMAL(20),
    
    /** Maison à consommation forte (40 kW) */
    FORTE(40);

    /** Demande énergétique en kilowatts (kW) */
    public final int demande;

    /**
     * Constructeur privé pour initialiser un type de maison avec sa demande.
     * 
     * @param demande la demande énergétique en kW
     */
    private TypeMaison(int demande){
        this.demande = demande;
    }
}
