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
java -cp bin net.reseau.electric.Main instance1.txt
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

## Collaborateurs
- Aminata Diallo
- Elodie Cao

## Commandes Git utiles
- `git clone <url>` : cloner le projet
- `git add .` : ajouter les fichiers
- `git commit -m "message"` : créer un commit
- `git push` : envoyer sur GitHub
