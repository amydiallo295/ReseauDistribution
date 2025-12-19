# Projet Réseau Électrique

Projet d'optimisation d'un réseau de distribution d'électricité pour la gestion de maisons et de générateurs.

## Informations pour l'évaluation

### Classe principale
**La méthode `main` se trouve dans la classe : `net.reseau.electric.Main`**

### Algorithme d'optimisation implémenté
**Oui, nous avons implémenté un algorithme plus efficace que l'algorithme naïf.**

Nous avons choisi d'implémenter l'**algorithme GRASP** (Greedy Randomized Adaptive Search Procedure).

**Pourquoi GRASP est plus efficace :**
- Évite les minima locaux grâce à la randomisation contrôlée
- Combine la rapidité d'une approche greedy avec la qualité d'une recherche locale
- Converge vers des solutions de meilleure qualité que l'approche naïve
- Paramétrable pour ajuster l'équilibre exploration/exploitation

### Fonctionnalités implémentées

#### Fonctionnalités correctement implémentées

1. **Lecture de fichier texte (importation du réseau)**
   - Parsing complet du format spécifié (generateur, maison, connexion)
   - Gestion des erreurs de format avec messages explicites
   - Validation des contraintes (ID uniques, types valides, capacités positives)
   - Gestion des exceptions : `IOException`, `FileNotFoundException`, `NumberFormatException`
   - Affichage du numéro de ligne en cas d'erreur de syntaxe

2. **Sauvegarde de la solution dans un fichier**
   - Export au format identique à l'import
   - Nom de fichier demandé à l'utilisateur
   - Gestion des erreurs d'écriture (IOException)
   - Message de confirmation après sauvegarde

3. **Paramètres de ligne de commande**
   - Support du chemin de fichier en 1er argument
   - Support du coefficient lambda en 2ème argument (optionnel)
   - Mode interactif si aucun argument fourni
   - Validation des arguments (fichier existant, lambda numérique)

4. **Menu interactif à 3 options**
   - Option 1 : Résolution automatique avec algorithme GRASP
   - Option 2 : Sauvegarde de la solution actuelle
   - Option 3 : Fin du programme
   - Gestion des choix invalides avec message d'erreur

5. **Algorithme de résolution automatique**
   - Implémentation complète de GRASP
   - Paramètres configurables (iterations, alpha, lambda)
   - Affichage du coût avant et après optimisation
   - Recherche locale pour améliorer la solution

6. **Robustesse et gestion des erreurs**
   - Fichier inexistant : FileNotFoundException avec message clair
   - Format de fichier incorrect : validation ligne par ligne
   - Entrées utilisateur invalides : InputMismatchException gérée
   - Options de menu inexistantes : boucle jusqu'à choix valide
   - Lambda invalide : validation et message d'erreur

7. **Interface textuelle**
   - Menus clairs et structurés
   - Messages explicites pour guider l'utilisateur
   - Affichage du réseau (générateurs et maisons)
   - Affichage des coûts et résultats d'optimisation

8. **Architecture et qualité du code**
   - Découpage en packages pertinent : `net.reseau.electric`, `algoOptimal`, `io`
   - Encapsulation correcte avec getters/setters
   - Conventions de nommage Java respectées
   - Pas d'erreur ni de warning à la compilation
   - Séparation des responsabilités (import/export, algorithme, modèle)

9. **Documentation**
   - Commentaires Javadoc pour toutes les classes
   - Documentation des attributs et méthodes
   - Explications sur les algorithmes utilisés
   - README complet avec instructions d'exécution

#### Fonctionnalités bonus implémentées

- **Tests unitaires JUnit 5** : Tests pour Generateur, Maison, et Reseau
- **7 fichiers d'instances** de test fournis
- **Compatibilité NORMAL/NORMALE** : Accepte les deux orthographes pour les types de maison

#### Problèmes ou limitations

**Aucun problème connu.** Toutes les fonctionnalités demandées sont implémentées et fonctionnent correctement.

## Structure du projet

```
ReseauDistribution/
├── src/              # Code source principal
│   └── net/reseau/electric/
│       ├── Main.java
│       ├── Reseau.java
│       ├── Maison.java
│       ├── Generateur.java
│       ├── TypeMaison.java
│       ├── algoOptimal/
│       │   └── AlgoOptimal.java
│       └── io/
│           ├── ReseauImporter.java
│           ├── ReseauExporter.java
│           └── instance*.txt (fichiers de test)
├── bin/              # Fichiers compilés (.class)
├── test/             # Tests unitaires
├── lib/              # Bibliothèques externes
└── .vscode/          # Configuration VS Code
```

## Compilation et exécution

### Compilation du projet
```bash
javac -d bin -sourcepath src src/net/reseau/electric/*.java src/net/reseau/electric/algoOptimal/*.java src/net/reseau/electric/io/*.java
```

### Exécution

#### Avec fichier d'instance (recommandé pour l'évaluation)
```bash
Syntaxe : java -cp bin net.reseau.electric.Main chemin/fichier/nom_instance.txt 
Exemple : java -cp bin net.reseau.electric.Main src/net/reseau/electric/io/instance1.txt 
```

#### Sans argument (mode manuel)
```bash
java -cp bin net.reseau.electric.Main
```

#### Menu interactif
Après le chargement, 3 options sont disponibles :
1. **Résolution automatique** : Optimise avec GRASP (demande iterations et alpha)
2. **Sauvegarder** : Exporte la solution dans un fichier
3. **Fin** : Quitte le programme

## Tests unitaires

### Compilation des tests
```bash
javac -cp "bin:lib/junit-platform-console-standalone-1.9.3.jar" -d bin test/net/reseau/electric/*.java
```

### Exécution des tests
```bash
java -jar lib/junit-platform-console-standalone-1.9.3.jar --class-path bin:test --scan-class-path
```

## Informations complémentaires

### Détails de l'algorithme GRASP

**Méthode** : `AlgoOptimal.resoudreOptimise(reseau, lambda, maxIterations, alpha)`

#### Principe de fonctionnement
- **Approche hybride** : Combine construction greedy randomisée et recherche locale itérative
- **Phase 1 - Construction greedy randomisée** :
  * Pour chaque maison (triées par demande décroissante)
  * Calcule le coût pour tous les générateurs possibles
  * Crée une liste restreinte de candidats (RCL) avec les meilleurs générateurs
  * Choisit aléatoirement un générateur dans la RCL
- **Phase 2 - Recherche locale** : 
  * Explore le voisinage en testant le déplacement de chaque maison
  * Garde les déplacements qui réduisent le coût
  * Itère jusqu'à convergence
- **Phase 3 - Mémorisation** :
  * Garde la meilleure solution parmi toutes les itérations
  * Restaure la meilleure solution à la fin

#### Avantages
- Évite les minima locaux grâce à la randomisation
- Converge vers des solutions de haute qualité
- Plus rapide et efficace que l'algorithme naïf

#### Paramètres recommandés
- **lambda** : 10000 (coefficient de pénalisation des surcharges)
- **iterations** : 10 à 20 (nombre de constructions-améliorations)
- **alpha** : 0.3 (degré de randomisation, entre 0 et 1)

#### Formule de coût
Le coût est calculé selon : **Coût = Dispersion + λ × Surcharge**
- **Dispersion** : Somme des écarts absolus entre le taux d'utilisation de chaque générateur et le taux moyen
- **Surcharge** : Somme des dépassements de capacité normalisés
- **λ (lambda)** : Coefficient de pénalisation (par défaut 10)

### Dossiers du projet

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
