package net.reseau.electric.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import net.reseau.electric.Reseau;
import net.reseau.electric.algoOptimal.AlgoOptimal;
import net.reseau.electric.io.ReseauExporter;
import net.reseau.electric.io.ReseauImporter;

import java.util.Optional;

public class MainController {

    @FXML
    private Button btnAjouterGenerateur, btnAjouterMaison, btnAjouterConnexion, btnFin;

    @FXML
    private TextArea textArea; // Pour afficher les infos du réseau

    private Reseau reseau = new Reseau();
    private int lambda = 10; // valeur par défaut

    @FXML
    private void initialize() {
        textArea.appendText("Réseau initialisé.\n");
    }

    @FXML
    private void ajouterGenerateur() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Ajouter un générateur (ex: G1 60)");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                try {
                    reseau.ajouterGenerateur(parts[0], Integer.parseInt(parts[1]));
                    textArea.appendText("Générateur ajouté : " + input + "\n");
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Capacité invalide, utilisez un entier.");
                }
            } else {
                showAlert("Erreur", "Format incorrect, utilisez 'Nom Capacité'.");
            }
        });
    }

    @FXML
    private void ajouterMaison() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Ajouter une maison (ex: M1 NORMALE)");
        dialog.setContentText("Types : BASSE, NORMALE, FORTE");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                reseau.ajouterMaison(parts[0], parts[1]);
                textArea.appendText("Maison ajoutée : " + input + "\n");
            } else {
                showAlert("Erreur", "Format incorrect, utilisez 'Nom Type'.");
            }
        });
    }

    @FXML
    private void ajouterConnexion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Ajouter une connexion (ex: M1 G1)");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                String h = parts[0].startsWith("M") ? parts[0] : parts[1];
                String g = parts[0].startsWith("G") ? parts[0] : parts[1];
                reseau.ajouterConnexion(h, g);
                textArea.appendText("Connexion ajoutée : " + h + " -> " + g + "\n");
            } else {
                showAlert("Erreur", "Format incorrect, utilisez 'Maison Générateur'.");
            }
        });
    }

    @FXML
    private void calculerCout() {
        reseau.calculerCout(); // méthode void
        textArea.appendText("Coût du réseau calculé (voir console si nécessaire).\n");
    }

    @FXML
    private void resoudreAutomatique() {
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setHeaderText("Nombre de tentatives d'échanges (k)");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                int k = Integer.parseInt(input);
                AlgoOptimal.resoudreOptimise(reseau, lambda, k);
                textArea.appendText("Résolution automatique effectuée avec k=" + k + "\n");
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Valeur de k invalide, utilisez un entier.");
            }
        });
    }

    @FXML
    private void sauvegarderReseau() {
        TextInputDialog dialog = new TextInputDialog("reseau.txt");
        dialog.setHeaderText("Nom du fichier de sauvegarde");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                ReseauExporter.exporter(reseau, input);
                textArea.appendText("Réseau sauvegardé dans " + input + "\n");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de sauvegarder le réseau : " + e.getMessage());
            }
        });
    }

    @FXML
    private void finProgramme() {
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
