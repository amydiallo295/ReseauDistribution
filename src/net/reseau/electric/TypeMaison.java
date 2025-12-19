package net.reseau.electric;

public enum TypeMaison {
    BASSE(10),
    NORMAL(20),
    FORTE(40);

    public final int demande;

    private TypeMaison(int demande){
        this.demande = demande;
    }
}
