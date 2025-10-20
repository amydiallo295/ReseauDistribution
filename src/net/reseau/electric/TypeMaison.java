public enum TypeMaison {
    BASSE(10),
    NORMALE(20),
    FORTE(40);

    public final int demande;

    private TypeMaison(int demande){
        this.demande = demande;
    }
}
