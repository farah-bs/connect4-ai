import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
# Recharger les données sans suppression, en forçant les colonnes numériques, mais sans dropna
df_strict = pd.read_csv("resultats_competition.csv")

# Conversion explicite des colonnes numériques
numeric_cols = ['ProfMinimax', 'ProfAlphaBeta', 'VictoireMinimax', 'VictoireAlphaBeta', 'MatchNul',
                'TempsMinimax(ms)', 'TempsAlphaBeta(ms)', 'NoeudsMinimax', 'NoeudsAlphaBeta']
df_strict[numeric_cols] = df_strict[numeric_cols].apply(pd.to_numeric, errors='coerce')

# Affichage complet sans supprimer de lignes
import ace_tools as tools; tools.display_dataframe_to_user(name="Résultats IA (version complète sans filtrage)", dataframe=df_strict)

# Visualisations
sns.set(style="whitegrid")

# Heatmap temps Minimax
pivot_temps = df_strict.pivot_table(index='ProfMinimax', columns='ProfAlphaBeta', values='TempsMinimax(ms)')
plt.figure(figsize=(8, 6))
sns.heatmap(pivot_temps, annot=True, fmt=".0f", cmap="Reds")
plt.title("Temps Minimax (ms) selon profondeur")
plt.xlabel("Profondeur AlphaBeta")
plt.ylabel("Profondeur Minimax")
plt.tight_layout()
plt.show()

# Heatmap nœuds Minimax
pivot_nodes = df_strict.pivot_table(index='ProfMinimax', columns='ProfAlphaBeta', values='NoeudsMinimax')
plt.figure(figsize=(8, 6))
sns.heatmap(pivot_nodes, annot=True, fmt=".0f", cmap="Blues")
plt.title("Nœuds Minimax selon profondeur")
plt.xlabel("Profondeur AlphaBeta")
plt.ylabel("Profondeur Minimax")
plt.tight_layout()
plt.show()

# Taux de victoire Minimax
df_strict['TauxVictoireMinimax'] = df_strict['VictoireMinimax'] / (
        df_strict['VictoireMinimax'] + df_strict['VictoireAlphaBeta'] + df_strict['MatchNul'])

plt.figure(figsize=(10, 6))
sns.lineplot(data=df_strict, x='ProfMinimax', y='TauxVictoireMinimax', hue='ProfAlphaBeta', marker='o')
plt.title("Taux de victoire Minimax en fonction des profondeurs")
plt.xlabel("Profondeur Minimax")
plt.ylabel("Taux de victoire")
plt.ylim(0, 1)
plt.tight_layout()
plt.show()
