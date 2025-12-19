package net.reseau.electric;

import java.util.Scanner;
import net.reseau.electric.io.ReseauImporter;
import net.reseau.electric.io.ReseauExporter;
import net.reseau.electric.algoOptimal.AlgoOptimal;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            String chemin = args[0];
            try {
                Reseau reseau = ReseauImporter.importer(chemin);
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("\nMenu :");
                    System.out.println("1) Résolution automatique");
                    System.out.println("2) Sauvegarder la solution actuelle");
                    System.out.println("3) Fin");
                    System.out.print("Votre choix : ");
                    String choix = scanner.nextLine();
                    switch (choix) {
                        case "1":
                            System.out.println("\n=== Résolution automatique (Algorithme GRASP) ===");
                            try {
                                System.out.print("Valeur de lambda : ");
                                int lambda = Integer.parseInt(scanner.nextLine());
                                System.out.print("Nombre d'itérations GRASP : ");
                                int iterations = Integer.parseInt(scanner.nextLine());
                                System.out.print("Paramètre alpha 0-1 (recommandé: 0.3) : ");
                                double alpha = Double.parseDouble(scanner.nextLine());
                                
                                System.out.println("\nCoût avant optimisation : " + reseau.calculerCoutTotal(lambda));
                                AlgoOptimal.resoudreOptimise(reseau, lambda, iterations, alpha);
                            } catch (NumberFormatException e) {
                                System.out.println("Erreur : Veuillez entrer des nombres valides.");
                            }
                            break;
                        case "2":
                            System.out.print("Nom du fichier de sauvegarde : ");
                            String nomFichier = scanner.nextLine();
                            try {
                                ReseauExporter.exporter(reseau, nomFichier);
                                System.out.println("Réseau sauvegardé dans " + nomFichier);
                            } catch (Exception e) {
                                System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
                            }
                            break;
                        case "3":
                            System.out.println("Fin du programme.");
                            scanner.close();
                            return;
                        default:
                            System.out.println("Choix invalide. Veuillez entrer 1, 2 ou 3.");
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            }
        } else {
            Reseau reseau = new Reseau();
            Scanner scanner = new Scanner(System.in);

            // Premier menu : création du réseau
            while (true) {
                System.out.println("\nMenu principal :");
                System.out.println("1) Ajouter un générateur");
                System.out.println("2) Ajouter une maison");
                System.out.println("3) Ajouter une connexion");
                System.out.println("4) Supprimer une connexion");
                System.out.println("5) Fin");
                System.out.print("Votre choix : ");
                String choix = scanner.nextLine();

                switch (choix) {
                    case "1":
                        System.out.print("Nom et capacité du générateur (ex: G1 60) : ");
                        String[] parts = scanner.nextLine().split(" ");
                        if (parts.length == 2) {
                            reseau.ajouterGenerateur(parts[0], Integer.parseInt(parts[1]));
                        }
                        break;
                    case "2":
                        System.out.print("Nom et type de la maison (ex: M1 NORMALE) : ");
                        System.out.print("\nTypes de maisons existantes : BASSE (10 kW), NORMALE (20 kW), FORTE (40 kW)\n");
                        String[] parts2 = scanner.nextLine().split(" ");
                        if (parts2.length == 2) {
                            reseau.ajouterMaison(parts2[0], parts2[1]);
                        }
                        break;

                    case "3":
                        // Afficher l'état des connexions
                        reseau.afficherEtatConnexions();
                        System.out.print("Connexion maison générateur (ex: M1 G1) : ");
                        String[] parts3 = scanner.nextLine().split(" ");
                        if (parts3.length == 2) {
                            String h = parts3[0].startsWith("M") ? parts3[0] : parts3[1];
                            String g = parts3[0].startsWith("G") ? parts3[0] : parts3[1];
                            reseau.ajouterConnexion(h, g);
                        }
                        break;

                    case "4":
                        // Afficher l'état des connexions
                        reseau.afficherConnexionsExistantes();
                        System.out.print("Connexion à supprimer (ex: M1 G1) : ");
                        String[] parts4 = scanner.nextLine().split(" ");
                        if (parts4.length == 2) {
                            String maison = parts4[0].startsWith("M") ? parts4[0] : parts4[1];
                            String generateur = parts4[0].startsWith("G") ? parts4[0] : parts4[1];
                            reseau.supprimerConnexion(maison, generateur);
                        }
                        break;
                    case "5":
                        // Vérifier que chaque maison est connectée à exactement un générateur
                        if (reseau.validerReseau()) {
                            System.out.println("Réseau validé avec succès ! Passage au menu de gestion du réseau.");
                            break;
                        } else {
                            System.out.println("\nProblème détecté dans le réseau. Veuillez corriger avant de continuer.");
                            continue; // Retour au menu principal
                        }

                    default:
                        System.out.println("Choix invalide. Veuillez entrer un numéro entre 1 et 5.");
                        break;
                }

                if (choix.equals("5") && reseau.validerReseau()) {
                    break;
                }
            }

            // Second menu : gestion du réseau
            while (true) {
                System.out.println("\nMenu réseau :");
                System.out.println("1) Calculer le coût du réseau électrique actuel");
                System.out.println("2) Modifier une connexion");
                System.out.println("3) Afficher le réseau");
                System.out.println("4) Fin");
                System.out.print("Votre choix : ");
                String choix = scanner.nextLine();

                switch (choix) {
                    case "1":
                        reseau.calculerCout();
                        break;
                    case "2":
                        // Afficher toutes les connexions existantes
                        reseau.afficherConnexionsExistantes();
                        System.out.print("Connexion à modifier (ex: M1 G1) : ");
                        String[] oldConn = scanner.nextLine().split(" ");
                        if (oldConn.length == 2) {
                            String h = oldConn[0].startsWith("M") ? oldConn[0] : oldConn[1];
                            if (!reseau.connexionExiste(h)) {
                                System.out.println("Erreur : cette connexion n'existe pas.");
                                break;
                            }
                            reseau.enleverConnexionMaison(h);
                            System.out.print("Nouvelle connexion (ex: M1 G2) : ");
                            String[] newConn = scanner.nextLine().split(" ");
                            if (newConn.length == 2) {
                                String newH = newConn[0].startsWith("M") ? newConn[0] : newConn[1];
                                String newG = newConn[0].startsWith("G") ? newConn[0] : newConn[1];
                                reseau.ajouterConnexion(newH, newG);
                            }
                        }
                        break;

                    case "3":
                        reseau.afficher();
                        break;

                    case "4":
                        System.out.println("Fin du programme.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Choix invalide. Veuillez entrer un numéro entre 1 et 4.");
                        break;
                }
            } // Fermeture de la boucle while du second menu
        }
    } // Fermeture de la méthode main
} // Fermeture de la classe
