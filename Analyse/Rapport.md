# Rapport de Projet IA : Puissance 4
Travail réalisé par : BEN SLAMA Farah


## 1. Introduction

Pour ce projet, j'ai choisi de travailler sur le jeu du Puissance 4, un classique des jeux de stratégie combinatoire. Il s’agit d’un jeu à deux joueurs à information complète, sans hasard, ce qui en fait un bon candidat pour l’étude d’algorithmes de recherche. L’objectif est d’implémenter et de comparer plusieurs algorithmes d’intelligence artificielle pour déterminer lequel est le plus performant dans ce contexte.

Les algorithmes implémentés puis évalués sont :
- Minimax
- Alpha-Bêta (optimisation du Minimax)
- MCTS (Monte Carlo Tree Search)

## 2. Présentation du jeu

Le Puissance 4 est un jeu de stratégie combinatoire abstrait. Il se joue sur une grille de 7 colonnes et 6 lignes. À tour de rôle, chaque joueur dépose un jeton de sa couleur dans une colonne de son choix. Le jeton tombe alors dans la position libre la plus basse de la colonne.

Un joueur gagne s’il parvient à aligner 4 jetons horizontalement, verticalement ou diagonalement.

### 3 Fonction d’évaluation simple

Cette fonction attribue des scores en fonction du nombre de pions alignés :
* +1 pour deux alignés
* +10 pour trois alignés
* +100 pour quatre alignés (victoire)

## 4. Implémentation des algorithmes

### 4.1 Minimax

L’algorithme explore récursivement les états possibles jusqu’à une profondeur fixée. À chaque niveau, le joueur cherche à maximiser ou minimiser la valeur selon qu’il est joueur max ou joueur min.

### 4.2 Alpha-Bêta

Cette version optimise Minimax en évitant d’explorer certaines branches inutiles grâce à des bornes alpha et bêta. Cela réduit significativement le nombre de noeuds explorés.

### 4.3 MCTS (Monte Carlo Tree Search)

MCTS repose sur quatre étapes :
Sélection d’un noeud à explorer via l’UCB
Expansion de l’arbre
Simulation d’une partie aléatoire
Rétropropagation du résultat

L’algorithme privilégie les coups les plus prometteurs selon les statistiques de victoires simulées.

## 5. Interface de jeu

Une interface graphique a été développée en Java Swing. Elle permet :
- de jouer humain contre humain/IA
- de faire s’affronter deux IA
- de choisir l’algorithme utilisé pour chaque joueur
- de régler la profondeur (pour Minimax et Alpha-Bêta) ou le nombre de simulations (pour MCTS)

## 6. Expériences et résultats
Afin d'avoir des résultats significatifs et comparables, j'ai doté chaque algorithme avec un hasard lors du choix des coups pour éviter la simulation de parties déterministes, inutiles pour l'évaluation des performances.

### 6.1 Comparaison Minimax vs Alpha-Bêta

Les tests effectués se trouvent dans le fichier `TestIAProfondeurMulti.java`.
Ce fichier exécute automatiquement une série de parties de Puissance 4 entre deux intelligences artificielles : l'une utilisant Minimax, l'autre Alpha-Beta.
Pour chaque combinaison de profondeurs d’algorithmes, plusieurs parties sont simulées afin d’évaluer leurs performances respectives selon plusieurs critères. Les résultats sont enregistrés dans le fichier resultats_ia.csv.

Le programme mesure pour chaque configuration :

- le nombre de victoires pour Minimax et Alpha-Beta,

- le nombre de matchs nuls,

- le temps moyen de calcul par coup pour chaque IA (en ms),

- le nombre moyen de nœuds explorés par partie.

Paramètres variés :
- Nombre de parties simulées : 5

- Profondeurs testées : 2, 4, 6, 8

- Algorithmes comparés :
* Minimax (joueur rouge)
* Alpha-Beta (joueur jaune)

J'ai effectué une exécution en multithread pour accélérer les simulations, vu que les profondeurs 6 et 8 prennent beaucoup de temps. 

Les résultats sont exportés dans le fichier `resultats_ia.csv`.

J'ai tracé les graphiques suivants pour visualiser les performances :
![](tempsexec_minmax_alphabeta.png)
![](12.png)
![](13..png)

## Temps d'exécution moyen

Les courbes et heatmaps montrent que :

- Le temps d'exécution de l'algorithme Minimax augmente très rapidement avec la profondeur. À partir de la profondeur 4, il dépasse la seconde, et à profondeur 8, il atteint plusieurs minutes.
- L'algorithme Alpha-Beta reste très rapide jusqu'à la profondeur 6. Même à profondeur 8, il reste beaucoup plus rapide que Minimax.
- À profondeur équivalente, Alpha-Beta est en général entre 10 et 1000 fois plus rapide que Minimax.

=> Alpha-Beta est beaucoup plus efficace que Minimax en termes de temps de réponse. Il permet de chercher plus profondément tout en conservant un temps de décision compatible avec un jeu interactif.

## Nombre de nœuds explorés

L’analyse du nombre de nœuds explorés révèle que :

- Minimax explore un nombre de nœuds qui augmente de façon exponentielle avec la profondeur. À profondeur 8, cela dépasse 50 millions de nœuds.
- Alpha-Beta explore beaucoup moins de nœuds grâce à l’élagage. Il réduit l’espace de recherche d’un facteur de 10 à 100 selon les cas.
- Plus la profondeur augmente, plus l’écart se creuse entre les deux algorithmes.

=> Alpha-Beta est considérablement plus économe que Minimax dans l’exploration de l’arbre de jeu. Cela permet une meilleure scalabilité de l’algorithme.

## Taux de victoire

L'analyse du taux de victoire montre que :

- Lorsque les deux algorithmes jouent à profondeur égale, leurs performances sont similaires en termes de victoires.
- Lorsque Alpha-Beta utilise une profondeur supérieure, il l’emporte régulièrement, même contre un Minimax qui explore plus lentement.
- L’avantage stratégique d’Alpha-Beta se manifeste surtout lorsqu’il peut jouer plus profondément que son adversaire.

=> En plus d’être plus rapide, Alpha-Beta est capable de prendre de meilleures décisions lorsqu’on lui permet de jouer avec une profondeur plus importante.

D'après les résultats interprétés, on peut conclure que :
- Alpha-Beta est plus rapide que Minimax à profondeur égale.
- Il explore beaucoup moins de nœuds, ce qui permet d'augmenter la profondeur sans exploser les ressources.
- Il obtient des performances similaires, voire supérieures, en termes de qualité de jeu.

Ainsi, l’algorithme Alpha-Beta surpasse Minimax aussi bien en efficacité qu’en potentiel stratégique, ce qui en fait le choix recommandé pour un joueur IA dans un jeu comme Puissance 4.


### 6.3 Analyse et variation des paramètres de l'algorithme MCTS
Le fichier `TestMCTSStats.java` exécute une série de tests pour analyser les performances de l’algorithme MCTS selon différents paramètres.

Trois paramètres sont variés :
- Le budget de simulations : 500, 1000, 5000
- La profondeur maximale des simulations : 5, 10, 20
- La constante d’exploration `C` : 0.5, √2, 3.0

Pour chaque combinaison :
- Un état initial est généré.
- Le meilleur coup est sélectionné via MCTS.
- Les données suivantes sont mesurées : coup choisi, nombre de simulations, taux de victoire estimé, et temps d’exécution.

Les résultats sont exportés dans le fichier `resultats_mcts.csv`.

J'ai tracé les graphiques suivants pour visualiser les performances :
![](mcts1.png)
![](mcts2.png)
![](mcts3.png)
![](mcts4.png)
![](mcts5.png)

Les figures présentées illustrent l'effet des différents paramètres de l'algorithme Monte Carlo Tree Search (MCTS) sur les performances observées, mesurées par le taux de victoire (WinRate).

* Impact du Budget (nombre de simulations autorisées)

L'augmentation du budget a un effet positif sur le taux de victoire, particulièrement pour des profondeurs de simulation faibles à moyennes :
- A SimDepth = 5, le gain est marginal mais présent.
- A SimDepth = 10, l'amélioration est plus significative.
- A SimDepth = 20, les performances sont déjà bonnes même à budget faible, mais progressent encore légèrement.

Cela montre que plus la profondeur est grande, plus MCTS exploite efficacement les simulations supplémentaires.

* Impact de la constante d'exploration C

On observe une tendance claire :
- Une constante C plus faible (0.5) donne de meilleurs résultats à grande profondeur (SimDepth = 20), suggérant que l’exploitation des nœuds prometteurs est préférable dans un budget contraint.
- A l'inverse, des constantes plus élevées pénalisent la performance à faible profondeur.

Cela confirme l'importance du réglage fin de C : plus C est grand, plus MCTS explore au risque de négliger l'exploitation locale.

* Temps d'exécution et efficacité

Le scatter plot entre Time(ms) et WinRate indique :
- Un nuage de points relativement cohérent pour SimDepth = 20, où MCTS reste efficace même en augmentant le temps de simulation.
- A SimDepth = 5, des temps plus longs n’apportent pas d'amélioration significative du taux de victoire.

Cela montre que l’effort de calcul est mieux rentabilisé à grande profondeur.

* Heatmap WinRate selon Budget et SimDepth

Cette carte montre clairement que :
- A budget fixe, augmenter SimDepth améliore nettement le taux de victoire.
- A Budget = 5000 et SimDepth = 20, on atteint les meilleurs scores, confirmant l’effet cumulé des deux facteurs.

* Nombre de simulations effectives

Le graphique montre une forte corrélation entre le nombre réel de simulations et le taux de victoire, en particulier pour SimDepth = 20.
- A faible profondeur, même un grand nombre de simulations ne suffit pas à compenser le manque d’information stratégique.

Ainsi on peut conclure que :
- La profondeur de simulation est le facteur le plus déterminant sur la qualité du jeu.
- Le budget améliore les résultats surtout si la profondeur est suffisante.
- La constante C doit être ajustée finement ; une valeur modérée (comme racine de 2) reste un bon compromis.
- Le temps de calcul est mieux utilisé si la profondeur est élevée.

Ces résultats confirment la puissance de MCTS lorsque ses paramètres sont bien calibrés, notamment en maximisant la profondeur tout en maintenant un budget suffisant.

## MCTS vs Alpha-Bêta vs Minimax
Pour évaluer les performances des algorithmes d’intelligence artificielle implémentés dans le cadre de ce projet de Puissance 4, une série d’expériences automatiques a été menée. Ces expériences consistent à faire s'affronter différentes IA entre elles dans des parties simulées, selon des combinaisons de paramètres variés (profondeur pour Minimax et Alpha-Bêta, budget de simulations pour MCTS).

Méthodologie

Chaque match oppose deux IA : une jouant en rouge, l’autre en jaune. Pour chaque partie jouée, les informations suivantes sont enregistrées dans un fichier CSV :
- le nom de chaque algorithme,
- sa configuration (profondeur ou budget),
- le vainqueur (Rouge, Jaune, ou Match nul),
- le temps d’exécution total pour chaque IA (en millisecondes),
- le nombre de nœuds explorés ou simulations effectuées.

Les tests couvrent :
- toutes les combinaisons de Minimax, Alpha-Bêta et MCTS en tant que joueur rouge ou jaune ;
- plusieurs niveaux de profondeur (2, 4, 6) pour Minimax et Alpha-Bêta ;
- plusieurs budgets de simulations (100 à 5000) pour MCTS ;
- un nombre fixe de 5 parties par configuration.

Résultats partiels et hypothèses

Par faute de temps, les analyses statistiques complètes (moyennes, écarts-types, heatmaps de performance) n'ont pas pu être réalisées. Cependant, des observations qualitatives et des hypothèses peuvent être formulées :

- Minimax, sans élagage, explore un très grand nombre de nœuds à partir de la profondeur 4, ce qui provoque des temps d'exécution élevés et rend les profondeurs supérieures coûteuses.
- Alpha-Bêta montre un comportement similaire à Minimax en termes de qualité de jeu, mais réduit significativement le nombre de nœuds explorés grâce à l'élagage, surtout en profondeur 4 ou plus.
- MCTS semble plus rapide que Minimax et Alpha-Bêta à profondeur/budget égal, notamment pour des budgets modérés (100 à 1000), mais reste plus aléatoire. Sa performance dépend fortement du nombre de simulations autorisées.

On peut raisonnablement supposer que :
- à budget ou profondeur faible, MCTS peut être compétitif voire supérieur à Minimax et Alpha-Bêta, notamment par sa capacité à échantillonner intelligemment.
- à profondeur élevée, Alpha-Bêta devient clairement plus efficace que Minimax grâce à la réduction du nombre de nœuds.
- Minimax brut est rapidement dépassé en efficacité par les deux autres algorithmes dès que la profondeur augmente.

Le code associé est disponible dans le fichier `TestIAProfondeurMulti.java`.

## 7. Conclusion
Ce projet a permis de mieux comprendre les stratégies de recherche dans les jeux à information complète. Alpha-Bêta montre des performances supérieures à Minimax grâce à la coupure. MCTS apporte une approche statistique efficace, notamment pour les profondeurs importantes.
