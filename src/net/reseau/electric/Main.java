import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Reseau reseau = new Reseau();
        Scanner scanner = new Scanner(System.in);

        // Premier menu : création du réseau
        while (true) {
            System.out.println("\nMenu principal :");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion");
            System.out.println("4) Fin");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();

            if (choix.equals("1")) {
                System.out.print("Nom et capacité du générateur (ex: G1 60) : ");
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length == 2) {
                    reseau.ajouterGenerateur(parts[0], Integer.parseInt(parts[1]));
                }
            } else if (choix.equals("2")) {
                System.out.print("Nom et type de la maison (ex: M1 NORMAL) : ");
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length == 2) {
                    reseau.ajouterMaison(parts[0], parts[1]);
                }
            } else if (choix.equals("3")) {
                System.out.print("Connexion maison générateur (ex: M1 G1) : ");
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length == 2) {
                    String h = parts[0].startsWith("M") ? parts[0] : parts[1];
                    String g = parts[0].startsWith("G") ? parts[0] : parts[1];
                    reseau.ajouterConnexion(h, g);
                }
            } else if (choix.equals("4")) {
                if (reseau.verifierConnexions()) break;
                else System.out.println("Corrigez les connexions avant de continuer.");
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

            if (choix.equals("1")) {
                reseau.calculerCout();
            } else if (choix.equals("2")) {
                System.out.print("Connexion à modifier (ex: M1 G1) : ");
                String[] oldConn = scanner.nextLine().split(" ");
                if (oldConn.length == 2) {
                    String h = oldConn[0].startsWith("M") ? oldConn[0] : oldConn[1];
                    if (!reseau.connexionExiste(h)) {
                        System.out.println("Erreur : cette connexion n'existe pas.");
                        continue;
                    }
                    reseau.enleverConnexion(h);
                    System.out.print("Nouvelle connexion (ex: M1 G2) : ");
                    String[] newConn = scanner.nextLine().split(" ");
                    if (newConn.length == 2) {
                        String newH = newConn[0].startsWith("M") ? newConn[0] : newConn[1];
                        String newG = newConn[0].startsWith("G") ? newConn[0] : newConn[1];
                        reseau.ajouterConnexion(newH, newG);
                    }
                }
            } else if (choix.equals("3")) {
                reseau.afficher();
            } else if (choix.equals("4")) {
                break;
            }
        }
        System.out.println("Fin du programme.");
        scanner.close();
    }
}