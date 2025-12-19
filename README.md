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

**Tests** 
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

## Algorithmes d'optimisation

### Implémentation
Oui, nous avons implémenté **trois algorithmes de résolution automatique** plus efficaces que l'algorithme naïf proposé :

#### 1. Algorithme GRASP (Greedy Randomized Adaptive Search Procedure)
- **Approche hybride** : Combine construction greedy randomisée et recherche locale
- **Phase 1 - Construction** : Pour chaque maison, crée une liste restreinte de candidats (RCL) avec les meilleurs générateurs, puis choisit aléatoirement
- **Phase 2 - Recherche locale** : Explore le voisinage pour améliorer la solution
- **Avantages** : Évite les minima locaux grâce à la randomisation, converge rapidement
- **Paramètres** : `alpha` (0-1) contrôle le degré de randomisation

#### 2. Génération de solution initiale Greedy
- **Approche constructive** : Construit une solution de qualité depuis zéro
- **Stratégie** : Trie les maisons par demande décroissante, assigne chaque maison au générateur qui minimise le coût global
- **Avantages** : Déterministe, rapide, produit directement une bonne solution initiale
- **Utilisation** : Peut servir de point de départ pour d'autres algorithmes

#### 3. Algorithme d'amélioration locale
- **Approche** : Recherche locale avec focus sur les surcharges
- **Stratégie** : 
  1. Identifie les générateurs surchargés
  2. Pour chaque maison sur générateur surchargé, teste tous les autres générateurs
  3. Garde le meilleur déplacement qui réduit le coût
  4. Itère jusqu'à convergence
- **Avantages** : Résout prioritairement les problèmes de surcharge

### Fonctionnement détaillé de l'algorithme principal

L'algorithme d'amélioration locale (`AlgoOptimal.resoudreOptimise()`) fonctionne comme suit :

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

5. **Algorithmes d'optimisation**
   - **GRASP** : Algorithme métaheuristique avec construction randomisée et recherche locale
   - **Génération de solution initiale greedy** : Construction intelligente d'une bonne solution de départ
   - **Recherche locale itérative** : Amélioration progressive avec focus sur les surcharges
   - Tous les algorithmes utilisent la même fonction de coût (Dispersion + λ × Surcharge)

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
