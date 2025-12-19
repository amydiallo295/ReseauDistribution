package net.reseau.electric.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.reseau.electric.Reseau;
import net.reseau.electric.algoOptimal.AlgoOptimal;
import net.reseau.electric.io.ReseauExporter;
import net.reseau.electric.io.ReseauImporter;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class MainFX extends Application {

    private Reseau reseau = new Reseau();
    private Stage stage;
    private TextArea logArea = new TextArea();
    private Label statusLabel = new Label("Prêt");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("⚡ Gestion Réseau Électrique - Version Professionnelle");
        
        // Rediriger System.out vers le journal d'activité
        redirectSystemOut();
        
        BorderPane mainLayout = createMainInterface();
        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.getStylesheets().add(getStyleSheet());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        log("✓ Application démarrée avec succès");
    }
    
    /**
     * Redirige la sortie console (System.out) vers le journal d'activité
     */
    private void redirectSystemOut() {
        PrintStream originalOut = System.out;
        
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            
            @Override
            public void write(int b) {
                char c = (char) b;
                buffer.append(c);
                
                if (c == '\n') {
                    final String text = buffer.toString();
                    buffer.setLength(0);
                    
                    // Écrire dans la console originale
                    originalOut.print(text);
                    
                    // Écrire dans le journal d'activité (sur le thread JavaFX)
                    Platform.runLater(() -> {
                        String cleanText = text.replace("\n", "").trim();
                        if (!cleanText.isEmpty()) {
                            logArea.appendText(cleanText + "\n");
                        }
                    });
                }
            }
        };
        
        System.setOut(new PrintStream(out, true));
    }

    private BorderPane createMainInterface() {
        BorderPane root = new BorderPane();
        
        // En-tête
        VBox header = createHeader();
        root.setTop(header);
        
        // Centre avec tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab manualTab = new Tab("📝 Création Manuelle", createManualPanel());
        Tab autoTab = new Tab("🤖 Import & Optimisation", createAutoPanel());
        Tab viewTab = new Tab("👁️ Visualisation", createViewPanel());
        
        tabPane.getTabs().addAll(manualTab, autoTab, viewTab);
        root.setCenter(tabPane);
        
        // Bas avec logs et status
        VBox bottom = createBottomPanel();
        root.setBottom(bottom);
        
        return root;
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");
        
        Label title = new Label("⚡ SYSTÈME DE GESTION RÉSEAU ÉLECTRIQUE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Optimisez la distribution d'énergie avec intelligence");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#e0e0e0"));
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createManualPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        
        // Section Générateurs
        TitledPane genSection = new TitledPane();
        genSection.setText("🔌 Gestion des Générateurs");
        genSection.setExpanded(true);
        
        GridPane genGrid = new GridPane();
        genGrid.setHgap(10);
        genGrid.setVgap(10);
        genGrid.setPadding(new Insets(10));
        
        TextField tfNomGen = new TextField();
        tfNomGen.setPromptText("Ex: G1, Gen1...");
        TextField tfCapGen = new TextField();
        tfCapGen.setPromptText("Ex: 100");
        
        Button btnAddGen = new Button("➕ Ajouter");
        btnAddGen.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddGen.setOnAction(e -> {
            String nom = tfNomGen.getText().trim();
            String cap = tfCapGen.getText().trim();
            if (!nom.isEmpty() && !cap.isEmpty()) {
                try {
                    reseau.ajouterGenerateur(nom, Integer.parseInt(cap));
                    log("✓ Générateur ajouté: " + nom + " (" + cap + " kW)");
                    tfNomGen.clear();
                    tfCapGen.clear();
                    updateStatus("Générateur " + nom + " ajouté");
                } catch (NumberFormatException ex) {
                    logError("✗ Capacité invalide");
                }
            } else {
                logError("✗ Veuillez remplir tous les champs");
            }
        });
        
        genGrid.add(new Label("Nom:"), 0, 0);
        genGrid.add(tfNomGen, 1, 0);
        genGrid.add(new Label("Capacité (kW):"), 0, 1);
        genGrid.add(tfCapGen, 1, 1);
        genGrid.add(btnAddGen, 2, 0, 1, 2);
        
        genSection.setContent(genGrid);
        
        // Section Maisons
        TitledPane maisonSection = new TitledPane();
        maisonSection.setText("🏠 Gestion des Maisons");
        maisonSection.setExpanded(true);
        
        GridPane maisonGrid = new GridPane();
        maisonGrid.setHgap(10);
        maisonGrid.setVgap(10);
        maisonGrid.setPadding(new Insets(10));
        
        TextField tfNomMaison = new TextField();
        tfNomMaison.setPromptText("Ex: M1, Maison1...");
        
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("BASSE (10 kW)", "NORMAL (20 kW)", "FORTE (40 kW)");
        cbType.setValue("NORMAL (20 kW)");
        cbType.setPrefWidth(200);
        
        Button btnAddMaison = new Button("➕ Ajouter");
        btnAddMaison.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddMaison.setOnAction(e -> {
            String nom = tfNomMaison.getText().trim();
            if (!nom.isEmpty()) {
                String type = cbType.getValue().split(" ")[0];
                reseau.ajouterMaison(nom, type);
                log("✓ Maison ajoutée: " + nom + " (Type: " + type + ")");
                tfNomMaison.clear();
                updateStatus("Maison " + nom + " ajoutée");
            } else {
                logError("✗ Veuillez entrer un nom");
            }
        });
        
        maisonGrid.add(new Label("Nom:"), 0, 0);
        maisonGrid.add(tfNomMaison, 1, 0);
        maisonGrid.add(new Label("Type:"), 0, 1);
        maisonGrid.add(cbType, 1, 1);
        maisonGrid.add(btnAddMaison, 2, 0, 1, 2);
        
        maisonSection.setContent(maisonGrid);
        
        // Section Connexions
        TitledPane connSection = new TitledPane();
        connSection.setText("🔗 Gestion des Connexions");
        connSection.setExpanded(true);
        
        GridPane connGrid = new GridPane();
        connGrid.setHgap(10);
        connGrid.setVgap(10);
        connGrid.setPadding(new Insets(10));
        
        TextField tfMaison = new TextField();
        tfMaison.setPromptText("Ex: M1");
        TextField tfGen = new TextField();
        tfGen.setPromptText("Ex: G1");
        
        Button btnConnect = new Button("🔗 Connecter");
        btnConnect.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConnect.setOnAction(e -> {
            String m = tfMaison.getText().trim();
            String g = tfGen.getText().trim();
            if (!m.isEmpty() && !g.isEmpty()) {
                reseau.ajouterConnexion(m, g);
                log("✓ Connexion: " + m + " ⟶ " + g);
                tfMaison.clear();
                tfGen.clear();
                updateStatus("Connexion créée");
            }
        });
        
        Button btnDisconnect = new Button("✂️ Supprimer");
        btnDisconnect.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDisconnect.setOnAction(e -> {
            String m = tfMaison.getText().trim();
            if (!m.isEmpty() && reseau.connexionExiste(m)) {
                reseau.enleverConnexionMaison(m);
                log("✓ Connexion supprimée pour: " + m);
                tfMaison.clear();
                updateStatus("Connexion supprimée");
            } else {
                logError("✗ Connexion inexistante");
            }
        });
        
        connGrid.add(new Label("Maison:"), 0, 0);
        connGrid.add(tfMaison, 1, 0);
        connGrid.add(new Label("Générateur:"), 0, 1);
        connGrid.add(tfGen, 1, 1);
        connGrid.add(btnConnect, 2, 0);
        connGrid.add(btnDisconnect, 2, 1);
        
        connSection.setContent(connGrid);
        
        // Boutons d'action
        VBox actionsContainer = new VBox(10);
        actionsContainer.setAlignment(Pos.CENTER);
        actionsContainer.setPadding(new Insets(15, 0, 0, 0));
        
        // Bouton vérifier surcharges
        Button btnCheckSurcharge = new Button("⚠️ Vérifier Surcharges");
        btnCheckSurcharge.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCheckSurcharge.setPrefWidth(200);
        btnCheckSurcharge.setOnAction(e -> {
            if (reseau.aSurcharge()) {
                logError("⚠️ ATTENTION: Le réseau contient des surcharges!");
                logError("   Modifiez les connexions avant de calculer le coût.");
                updateStatus("Surcharges détectées!");
            } else {
                log("✓ Aucune surcharge détectée");
                updateStatus("Aucune surcharge");
            }
        });
        
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        
        Button btnCalc = new Button("💰 Calculer Coût");
        btnCalc.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCalc.setPrefWidth(150);
        btnCalc.setOnAction(e -> {
            if (reseau.aSurcharge()) {
                logError("✗ Impossible de calculer le coût: le réseau contient des surcharges!");
                logError("   Veuillez modifier les connexions avant de continuer.");
                updateStatus("Calcul bloqué - Surcharges présentes");
            } else {
                reseau.calculerCout();
                log("✓ Calcul du coût effectué");
                updateStatus("Coût calculé");
            }
        });
        
        Button btnValidate = new Button("✓ Valider Réseau");
        btnValidate.setStyle("-fx-background-color: #00BCD4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnValidate.setPrefWidth(150);
        btnValidate.setOnAction(e -> {
            if (reseau.validerReseau()) {
                log("✓ Réseau valide!");
                updateStatus("Réseau validé avec succès");
            } else {
                logError("✗ Réseau invalide");
                updateStatus("Erreurs de validation");
            }
        });
        
        actions.getChildren().addAll(btnCalc, btnValidate);
        actionsContainer.getChildren().addAll(btnCheckSurcharge, actions);
        
        panel.getChildren().addAll(genSection, maisonSection, connSection, actionsContainer);
        return panel;
    }

    private VBox createAutoPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        
        // Section Import
        TitledPane importSection = new TitledPane();
        importSection.setText("📂 Import de Fichier");
        importSection.setExpanded(true);
        
        VBox importBox = new VBox(10);
        importBox.setPadding(new Insets(10));
        
        Label fileLabel = new Label("Aucun fichier sélectionné");
        fileLabel.setStyle("-fx-font-style: italic;");
        
        Button btnImport = new Button("📁 Choisir un fichier...");
        btnImport.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-weight: bold;");
        btnImport.setPrefWidth(200);
        btnImport.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Sélectionner un fichier réseau");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                try {
                    reseau = ReseauImporter.importer(file.getAbsolutePath());
                    fileLabel.setText("✓ Fichier chargé: " + file.getName());
                    fileLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    log("✓ Import réussi: " + file.getName());
                    updateStatus("Fichier importé");
                } catch (Exception ex) {
                    fileLabel.setText("✗ Erreur: " + ex.getMessage());
                    fileLabel.setStyle("-fx-text-fill: red;");
                    logError("✗ Erreur d'import: " + ex.getMessage());
                }
            }
        });
        
        importBox.getChildren().addAll(btnImport, fileLabel);
        importSection.setContent(importBox);
        
        // Section Optimisation
        TitledPane optSection = new TitledPane();
        optSection.setText("⚙️ Optimisation Automatique");
        optSection.setExpanded(true);
        
        GridPane optGrid = new GridPane();
        optGrid.setHgap(15);
        optGrid.setVgap(15);
        optGrid.setPadding(new Insets(10));
        
        Label lblLambda = new Label("λ (Lambda - pénalisation):");
        lblLambda.setStyle("-fx-font-weight: bold;");
        TextField tfLambda = new TextField("10");
        tfLambda.setPromptText("Recommandé: 10");
        tfLambda.setPrefWidth(150);
        
        Label lblK = new Label("k (Nombre d'itérations):");
        lblK.setStyle("-fx-font-weight: bold;");
        TextField tfK = new TextField("100");
        tfK.setPromptText("Recommandé: 100");
        tfK.setPrefWidth(150);
        
        Button btnOptimize = new Button("🚀 Lancer l'Optimisation");
        btnOptimize.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        btnOptimize.setPrefWidth(250);
        btnOptimize.setPrefHeight(40);
        btnOptimize.setOnAction(e -> {
            try {
                int lambda = Integer.parseInt(tfLambda.getText());
                int k = Integer.parseInt(tfK.getText());
                log("⚙️ Démarrage optimisation (λ=" + lambda + ", k=" + k + ")...");
                updateStatus("Optimisation en cours...");
                AlgoOptimal.resoudreOptimise(reseau, lambda, k);
                log("✓ Optimisation terminée avec succès!");
                updateStatus("Optimisation terminée");
            } catch (NumberFormatException ex) {
                logError("✗ Paramètres invalides");
            }
        });
        
        optGrid.add(lblLambda, 0, 0);
        optGrid.add(tfLambda, 1, 0);
        optGrid.add(lblK, 0, 1);
        optGrid.add(tfK, 1, 1);
        optGrid.add(btnOptimize, 0, 2, 2, 1);
        GridPane.setHalignment(btnOptimize, javafx.geometry.HPos.CENTER);
        
        optSection.setContent(optGrid);
        
        // Section Export
        TitledPane exportSection = new TitledPane();
        exportSection.setText("💾 Export de la Solution");
        exportSection.setExpanded(true);
        
        VBox exportBox = new VBox(10);
        exportBox.setPadding(new Insets(10));
        exportBox.setAlignment(Pos.CENTER);
        
        Button btnExport = new Button("💾 Sauvegarder le réseau...");
        btnExport.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnExport.setPrefWidth(250);
        btnExport.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Sauvegarder le réseau");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
            fc.setInitialFileName("reseau_optimise.txt");
            File file = fc.showSaveDialog(stage);
            if (file != null) {
                try {
                    ReseauExporter.exporter(reseau, file.getAbsolutePath());
                    log("✓ Réseau sauvegardé: " + file.getName());
                    updateStatus("Export réussi");
                } catch (Exception ex) {
                    logError("✗ Erreur d'export: " + ex.getMessage());
                }
            }
        });
        
        exportBox.getChildren().add(btnExport);
        exportSection.setContent(exportBox);
        
        // Boutons d'action globaux
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button btnCalc = new Button("💰 Calculer Coût");
        btnCalc.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCalc.setPrefWidth(150);
        btnCalc.setOnAction(e -> {
            reseau.calculerCout();
            log("✓ Calcul du coût effectué");
            updateStatus("Coût calculé");
        });
        
        Button btnValidate = new Button("✓ Valider Réseau");
        btnValidate.setStyle("-fx-background-color: #00BCD4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnValidate.setPrefWidth(150);
        btnValidate.setOnAction(e -> {
            if (reseau.validerReseau()) {
                log("✓ Réseau valide!");
                updateStatus("Réseau validé avec succès");
            } else {
                logError("✗ Réseau invalide");
                updateStatus("Erreur: réseau invalide");
            }
        });
        
        actionBox.getChildren().addAll(btnCalc, btnValidate);
        
        panel.getChildren().addAll(importSection, optSection, exportSection, actionBox);
        return panel;
    }

    private VBox createViewPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        
        // Zone de texte pour affichage (déclarée en premier pour être accessible dans les boutons)
        TextArea displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setWrapText(true);
        displayArea.setPrefHeight(400);
        displayArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        displayArea.setPromptText("Les informations du réseau s'afficheront ici...");
        
        // Boutons d'affichage
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button btnShowNetwork = new Button("🔍 Afficher Réseau Complet");
        btnShowNetwork.setStyle("-fx-background-color: #009688; -fx-text-fill: white; -fx-font-weight: bold;");
        btnShowNetwork.setPrefWidth(200);
        btnShowNetwork.setOnAction(e -> {
            displayArea.setText(reseau.getReseauAsString());
            log("✓ Affichage du réseau");
        });
        
        Button btnShowConnections = new Button("🔗 Voir Connexions");
        btnShowConnections.setStyle("-fx-background-color: #00BCD4; -fx-text-fill: white; -fx-font-weight: bold;");
        btnShowConnections.setPrefWidth(200);
        btnShowConnections.setOnAction(e -> {
            displayArea.setText(reseau.getConnexionsAsString());
            log("✓ Affichage des connexions");
        });
        
        Button btnShowState = new Button("📊 État des Connexions");
        btnShowState.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-weight: bold;");
        btnShowState.setPrefWidth(200);
        btnShowState.setOnAction(e -> {
            displayArea.setText(reseau.getEtatConnexionsAsString());
            log("✓ Affichage de l'état");
        });
        
        buttonBox.getChildren().addAll(btnShowNetwork, btnShowConnections, btnShowState);
        
        Label infoLabel = new Label("ℹ️ Utilisez les boutons ci-dessus pour visualiser différentes vues du réseau");
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        
        // Boutons d'action globaux
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button btnCalc = new Button("💰 Calculer Coût");
        btnCalc.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCalc.setPrefWidth(150);
        btnCalc.setOnAction(e -> {
            reseau.calculerCout();
            log("✓ Calcul du coût effectué");
            updateStatus("Coût calculé");
        });
        
        Button btnValidate = new Button("✓ Valider Réseau");
        btnValidate.setStyle("-fx-background-color: #00BCD4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnValidate.setPrefWidth(150);
        btnValidate.setOnAction(e -> {
            if (reseau.validerReseau()) {
                log("✓ Réseau valide!");
                updateStatus("Réseau validé avec succès");
            } else {
                logError("✗ Réseau invalide");
                updateStatus("Erreur: réseau invalide");
            }
        });
        
        actionBox.getChildren().addAll(btnCalc, btnValidate);
        
        panel.getChildren().addAll(infoLabel, buttonBox, displayArea, actionBox);
        return panel;
    }

    private VBox createBottomPanel() {
        VBox bottom = new VBox(5);
        
        // Zone de logs
        Label logTitle = new Label("📋 Journal d'activité:");
        logTitle.setStyle("-fx-font-weight: bold;");
        
        logArea.setEditable(false);
        logArea.setPrefHeight(120);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 11px;");
        
        // Barre de status
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        
        statusLabel.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label("⏰ " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        Button btnExit = new Button("🚪 Fin");
        btnExit.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnExit.setOnAction(e -> {
            log("👋 Fermeture de l'application...");
            stage.close();
        });
        
        statusBar.getChildren().addAll(statusLabel, spacer, timeLabel, btnExit);
        
        VBox logBox = new VBox(5, logTitle, logArea);
        logBox.setPadding(new Insets(10));
        
        bottom.getChildren().addAll(logBox, statusBar);
        return bottom;
    }

    private void log(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.appendText("[" + timestamp + "] " + message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    private void logError(String message) {
        log("❌ " + message);
    }

    private void updateStatus(String status) {
        statusLabel.setText("📌 " + status);
    }

    private String getStyleSheet() {
        return "data:text/css," +
            ".tab-pane { -fx-background-color: #fafafa; }" +
            ".tab { -fx-background-color: #e0e0e0; -fx-font-weight: bold; }" +
            ".tab:selected { -fx-background-color: white; }" +
            ".titled-pane { -fx-font-size: 13px; }" +
            ".titled-pane > .title { -fx-background-color: #f0f0f0; -fx-font-weight: bold; }" +
            ".button { -fx-cursor: hand; }" +
            ".text-field { -fx-border-color: #bbb; -fx-border-radius: 3; }";
    }
}
