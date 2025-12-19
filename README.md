# ReseauDistribution

Projet Java : Réseau de distribution d'électricité

## Description
Application pour gérer et simuler la distribution d'électricité avec import/export de fichiers et algorithme d'optimisation.

## Exécution du programme

### Classe principale
La méthode `main` se trouve dans la classe : **`net.reseau.electric.Main`**

### Compilation
```bash
javac -d bin -sourcepath src src/net/reseau/electric/*.java src/net/reseau/electric/algoOptimal/*.java src/net/reseau/electric/io/*.java
```

### Exécution

#### Mode console

**Sans argument** (mode interactif) :
```bash
java -cp bin net.reseau.electric.Main
```

**Avec argument** (import d'un fichier) :
```bash
java -cp bin net.reseau.electric.Main <chemin_fichier>
```

Exemple :
```bash
java -cp bin net.reseau.electric.Main src/net/reseau/electric/io/instance1.txt
```

#### Interface graphique (JavaFX)

**Note importante** : L'interface graphique nécessite JavaFX qui n'est pas inclus dans le projet à part les fichiers .jar

**Résumé rapide :**

1. Téléchargez JavaFX SDK 25.0.1 depuis https://gluonhq.com/products/javafx/ (choisir la version correspondant au système)

2. Compilation avec JavaFX :
```bash
# macOS/Linux
# Remplacer /chemin/vers/javafx-sdk-25.0.1/lib par le chemin réel
javac --module-path "/chemin/vers/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -d bin src/net/reseau/electric/*.java src/net/reseau/electric/**/*.java

# Exemple macOS :
# javac --module-path "/Users/votreNom/Downloads/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -d bin src/net/reseau/electric/*.java src/net/reseau/electric/**/*.java

# Windows
javac --module-path "C:\chemin\vers\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml -d bin src/net/reseau/electric/*.java src/net/reseau/electric/**/*.java
```

**Note macOS** : Si vous obtenez "no matches found" ou "Operation not permitted", il faut :
- Utiliser des guillemets autour du chemin JavaFX (comme dans l'exemple)
- Ne PAS copier-coller avec des backslashes `\` devant les options
- Accorder l'accès au Terminal dans Préférences Système > Confidentialité > Accès complet au disque

3. Exécution de l'interface graphique :
```bash
# macOS/Linux
java --module-path "/chemin/vers/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml -cp bin net.reseau.electric.gui.MainFX

# Windows
java --module-path "C:\chemin\vers\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp bin net.reseau.electric.gui.MainFX
```

#### Tests 
```bash
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path bin:test --scan-class-path
```

## Structure du projet
```
src/
└── net/
    └── reseau/
        └── electric/
            ├── Main.java           (Point d'entrée du programme)
            ├── Reseau.java         (Gestion du réseau électrique)
            ├── Generateur.java     (Classe générateur)
            ├── Maison.java         (Classe maison)
            └── TypeMaison.java     (Énumération des types de maison)
            ├── algoOptimal/
            │   └── AlgoOptimal.java    (Algorithme d'optimisation)
            └── io/
                ├── ReseauExporter.java (Export vers fichier)
                └── ReseauImporter.java (Import depuis fichier)
bin/                                (Fichiers .class compilés)
```

## Algorithme d'optimisation

### Implémentation
Oui, nous avons implémenté un algorithme de résolution automatique plus efficace : **Algorithme de recherche locale avec amélioration itérative**.

### Fonctionnement
L'algorithme (`AlgoOptimal.resoudreOptimise()`) fonctionne comme suit :

1. **Identification des surcharges** : Détecte tous les générateurs dont la charge dépasse la capacité
2. **Recherche du meilleur déplacement** : Pour chaque maison connectée à un générateur surchargé :
   - Teste sa connexion à tous les autres générateurs
   - Vérifie que le générateur cible ne sera pas surchargé
   - Calcule le coût total après le déplacement
   - Conserve le meilleur déplacement trouvé
3. **Application de la meilleure solution** : Effectue le déplacement qui réduit le plus le coût
4. **Itération** : Répète jusqu'à ce qu'aucune amélioration ne soit trouvée ou que le nombre maximum d'itérations soit atteint

### Formule de coût
Le coût est calculé selon : **Coût = Dispersion + λ × Surcharge**
- **Dispersion** : Somme des écarts absolus entre le taux d'utilisation de chaque générateur et le taux moyen
- **Surcharge** : Somme des dépassements de capacité normalisés
- **λ (lambda)** : Coefficient de pénalisation (par défaut 10)

## Fonctionnalités implémentées

### Fonctionnalités complètes

1. **Gestion du réseau**
   - Ajout de générateurs avec capacité
   - Ajout de maisons avec 3 types (BASSE: 10kW, NORMALE: 20kW, FORTE: 40kW)
   - Ajout de connexions maison-générateur
   - Suppression de connexions
   - Modification de connexions

2. **Validation du réseau**
   - Vérification qu'au moins 1 maison et 1 générateur existent
   - Vérification que chaque maison est connectée à exactement 1 générateur
   - Vérification que la somme des demandes ≤ somme des capacités
   - Détection des surcharges avec avertissements

3. **Calcul de coût**
   - Calcul de la dispersion
   - Calcul de la surcharge
   - Coût total avec coefficient lambda configurable

4. **Import/Export**
   - Export du réseau vers un fichier texte (format Prolog-like)
   - Import du réseau depuis un fichier texte
   - Validation de la syntaxe lors de l'import

5. **Algorithme d'optimisation**
   - Recherche locale itérative
   - Résolution des surcharges prioritaire
   - Convergence vers un minimum local

6. **Interface utilisateur**
   - Menu principal pour créer le réseau
   - Menu secondaire pour gérer et optimiser
   - Affichage détaillé de l'état du réseau
   - Messages d'erreur explicites

## Informations complémentaires

## Dossiers du projet

- **`lib/`** : Contient les bibliothèques externes nécessaires au projet
  - `junit-platform-console-standalone-1.9.3.jar` : Framework JUnit 5 pour l'exécution des tests unitaires

- **`.vscode/`** : Contient la configuration spécifique à l'éditeur Visual Studio Code
  - Paramètres de l'éditeur
  - Configurations de débogage
  - Extensions recommandées
  - Paramètres du workspace

- **`test/`** : Contient tous les tests unitaires du projet
  - `GenerateurTest.java` : Tests pour la classe Generateur
  - `MaisonTest.java` : Tests pour la classe Maison
  - `ReseauTest.java` : Tests pour la classe Reseau
  - Utilise JUnit 5 comme framework de tests

## Collaborateurs
- Aminata Diallo
- Elodie Cao

## Commandes Git utiles
- `git clone <url>` : cloner le projet
- `git add .` : ajouter les fichiers
- `git commit -m "message"` : créer un commit
- `git push` : envoyer sur GitHub
