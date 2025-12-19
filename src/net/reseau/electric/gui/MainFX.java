package net.reseau.electric.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.reseau.electric.Reseau;
import net.reseau.electric.algoOptimal.AlgoOptimal;
import net.reseau.electric.io.ReseauExporter;
import net.reseau.electric.io.ReseauImporter;

import java.io.File;

public class MainFX extends Application {

    private Reseau reseau = new Reseau();
    private Stage stage;
    private TextArea textArea = new TextArea();

    @FXML
    private TextField tfNomGenerateur, tfCapaciteGenerateur, tfNomMaison, tfMaisonConnexion, tfGenerateurConnexion;
    @FXML
    private ComboBox<String> cbTypeMaison = new ComboBox<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Gestion Réseau Électrique");

        textArea.setEditable(false);
        textArea.setPrefHeight(300);

        ChoiceDialog<String> modeDialog = new ChoiceDialog<>("Manuel", "Manuel", "Automatique");
        modeDialog.setTitle("Choix du mode");
        modeDialog.setHeaderText("Choisissez le mode de réseau");
        modeDialog.setContentText("Mode :");

        modeDialog.showAndWait().ifPresent(mode -> {
            if (mode.equals("Manuel")) {
                VBox manuelLayout = createManuelLayout();
                primaryStage.setScene(new Scene(manuelLayout, 650, 500));
                primaryStage.show();
            } else {
                VBox autoLayout = createAutoLayout();
                primaryStage.setScene(new Scene(autoLayout, 600, 400));
                primaryStage.show();
            }
        });
    }

    // ==================== Méthode pour le mode manuel ====================
    private VBox createManuelLayout() {
        // Initialisation ComboBox types de maison
        cbTypeMaison.getItems().addAll("BASSE", "NORMAL", "FORTE");
        cbTypeMaison.setValue("NORMAL");

        tfNomGenerateur = new TextField();
        tfCapaciteGenerateur = new TextField();
        tfNomMaison = new TextField();
        tfMaisonConnexion = new TextField();
        tfGenerateurConnexion = new TextField();

        Button btnAjouterGenerateur = new Button("Ajouter Générateur");
        btnAjouterGenerateur.setOnAction(e -> ajouterGenerateur());

        Button btnAjouterMaison = new Button("Ajouter Maison");
        btnAjouterMaison.setOnAction(e -> ajouterMaison());

        Button btnAjouterConnexion = new Button("Ajouter Connexion");
        btnAjouterConnexion.setOnAction(e -> ajouterConnexion());

        Button btnSupprimerConnexion = new Button("Supprimer Connexion");
        btnSupprimerConnexion.setOnAction(e -> supprimerConnexion());

        Button btnModifierConnexion = new Button("Modifier Connexion");
        btnModifierConnexion.setOnAction(e -> modifierConnexion());

        Button btnCalculerCout = new Button("Calculer Coût");
        btnCalculerCout.setOnAction(e -> textArea.appendText(reseau.calculerCoutString() + "\n"));

        Button btnAfficherReseau = new Button("Afficher Réseau");
        btnAfficherReseau.setOnAction(e -> textArea.appendText(reseau.afficherString() + "\n"));

        Button btnFin = new Button("Fin");
        btnFin.setOnAction(e -> stage.close());

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);

        // Ajouter tous les champs et boutons
        grid.add(new Label("Nom Générateur:"), 0, 0);
        grid.add(tfNomGenerateur, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(tfCapaciteGenerateur, 1, 1);
        grid.add(btnAjouterGenerateur, 2, 0, 1, 2);

        grid.add(new Label("Nom Maison:"), 0, 2);
        grid.add(tfNomMaison, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(cbTypeMaison, 1, 3);
        grid.add(btnAjouterMaison, 2, 2, 1, 2);

        grid.add(new Label("Maison à Connecter:"), 0, 4);
        grid.add(tfMaisonConnexion, 1, 4);
        grid.add(new Label("Générateur à Connecter:"), 0, 5);
        grid.add(tfGenerateurConnexion, 1, 5);
        grid.add(btnAjouterConnexion, 2, 4);


        HBox hBoxBottom = new HBox(
        5,
        btnCalculerCout,
        btnSupprimerConnexion,
        btnModifierConnexion,
        btnAfficherReseau,
        btnFin
);

        VBox layout = new VBox(10, grid, textArea, hBoxBottom);

        return layout;
    }

    // ==================== Méthode pour le mode automatique ====================
    private VBox createAutoLayout() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir fichier réseau");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                reseau = ReseauImporter.importer(file.getAbsolutePath());
                textArea.appendText("Fichier chargé : " + file.getName() + "\n");

                TextInputDialog lambdaDialog = new TextInputDialog("10");
                lambdaDialog.setTitle("Paramètre λ");
                lambdaDialog.setHeaderText("Entrez la valeur de λ (lambda)");
                lambdaDialog.setContentText("λ :");
                int lambda = lambdaDialog.showAndWait().map(Integer::parseInt).orElse(10);

                TextInputDialog kDialog = new TextInputDialog("100");
                kDialog.setTitle("Nombre de tentatives");
                kDialog.setHeaderText("Entrez le nombre de tentatives (k)");
                kDialog.setContentText("k :");
                int k = kDialog.showAndWait().map(Integer::parseInt).orElse(100);

                AlgoOptimal.resoudreOptimise(reseau, lambda, k);
                textArea.appendText("Optimisation terminée.\n");
            } catch (Exception e) {
                textArea.appendText("Erreur : " + e.getMessage() + "\n");
            }
        } else {
            textArea.appendText("Aucun fichier sélectionné.\n");
        }

        Button btnSauvegarder = new Button("Sauvegarder Réseau");
        btnSauvegarder.setOnAction(e -> {
            FileChooser saveChooser = new FileChooser();
            saveChooser.setTitle("Sauvegarder le réseau");
            File f = saveChooser.showSaveDialog(stage);
            if (f != null) {
                try {
                    ReseauExporter.exporter(reseau, f.getAbsolutePath());
                    textArea.appendText("Réseau sauvegardé dans " + f.getName() + "\n");
                } catch (Exception ex) {
                    textArea.appendText("Erreur : " + ex.getMessage() + "\n");
                }
            }
        });

        Button btnFin = new Button("Fin");
        btnFin.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, textArea, btnSauvegarder, btnFin);
        return layout;
    }

    // ==================== Fonctions boutons Manuel ====================
    private void ajouterGenerateur() {
        String nom = tfNomGenerateur.getText().trim();
        String capStr = tfCapaciteGenerateur.getText().trim();
        
        if (nom.isEmpty() || capStr.isEmpty()) {
            textArea.appendText("Erreur : nom et capacité requis.\n");
            return;
        }
        try {
            int capacite = Integer.parseInt(capStr);
            reseau.ajouterGenerateur(nom, capacite);
            textArea.appendText("Générateur ajouté : " + nom + " - " + capacite + "\n");
            // Vider les champs
            tfNomGenerateur.clear();
            tfCapaciteGenerateur.clear();
        } catch (NumberFormatException e) {
            textArea.appendText("Erreur : capacité invalide.\n");
        }
    }

    private void ajouterMaison() {
        String nom = tfNomMaison.getText().trim();
        String type = cbTypeMaison.getValue();
        if (nom.isEmpty()) {
            textArea.appendText("Erreur : nom de la maison requis.\n");
            return;
        }
        reseau.ajouterMaison(nom, type);
        textArea.appendText("Maison ajoutée : " + nom + " - " + type + "\n");
        // Vider les champs
        tfNomMaison.clear();
        cbTypeMaison.setValue("NORMALE");
    }

    @FXML
    private void ajouterConnexion() {
        String maison = tfMaisonConnexion.getText().trim();
        String generateur = tfGenerateurConnexion.getText().trim();
        if (maison.isEmpty() || generateur.isEmpty()) {
            textArea.appendText("Erreur : maison et générateur requis.\n");
            return;
        }
        reseau.ajouterConnexion(maison, generateur);
        textArea.appendText("Connexion ajoutée : " + maison + " -> " + generateur + "\n");
        tfMaisonConnexion.clear();
        tfGenerateurConnexion.clear();
    }

    private void supprimerConnexion() {
        String maison = tfMaisonConnexion.getText().trim();
        if (!reseau.connexionExiste(maison)) {
            textArea.appendText("Erreur : cette connexion n'existe pas.\n");
            return;
        }
        reseau.enleverConnexionMaison(maison);
        textArea.appendText("Connexion supprimée pour : " + maison + "\n");
        viderChamps();
    }

    private void modifierConnexion() {
        String maison = tfMaisonConnexion.getText().trim();
        String generateur = tfGenerateurConnexion.getText().trim();
        if (!reseau.connexionExiste(maison)) {
            textArea.appendText("Erreur : cette connexion n'existe pas.\n");
            return;
        }
        reseau.enleverConnexionMaison(maison);
        reseau.ajouterConnexion(maison, generateur);
        textArea.appendText("Connexion modifiée : " + maison + " -> " + generateur + "\n");
        viderChamps();
    }



}

    