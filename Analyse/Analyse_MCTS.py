import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Charger les données
df_strict = pd.read_csv("resultats_mcts.csv", sep=",")  # ou sep=";" selon le fichier réel

# Remplacer les virgules décimales par des points si besoin
df_strict = df_strict.applymap(lambda x: str(x).replace(",", ".") if isinstance(x, str) else x)

# Conversion explicite des colonnes numériques
numeric_cols = ['Budget', 'SimDepth', 'C', 'SelectedMove', 'Simulations', 'WinRate', 'Time(ms)']
df_strict[numeric_cols] = df_strict[numeric_cols].apply(pd.to_numeric, errors='coerce')

sns.set(style="whitegrid")

# Courbes d’évolution du taux de victoire (WinRate)
plt.figure(figsize=(10, 6))
sns.lineplot(data=df_strict, x='Budget', y='WinRate', hue='SimDepth', marker='o')
plt.title("Taux de victoire (WinRate) en fonction du Budget et de la profondeur")
plt.xlabel("Budget")
plt.ylabel("Taux de victoire (WinRate)")
plt.ylim(0, 1)
plt.tight_layout()
plt.show()

# Courbe secondaire : WinRate en fonction de C (facultatif)
plt.figure(figsize=(10, 6))
sns.lineplot(data=df_strict, x='C', y='WinRate', hue='SimDepth', marker='x')
plt.title("Taux de victoire (WinRate) en fonction de la constante d'exploration C")
plt.xlabel("Constante C")
plt.ylabel("Taux de victoire (WinRate)")
plt.ylim(0, 1)
plt.tight_layout()
plt.show()

# Scatter plot WinRate vs Time(ms)
plt.figure(figsize=(10, 6))
sns.scatterplot(data=df_strict, x='Time(ms)', y='WinRate', hue='C', style='SimDepth', s=100)
plt.title("Taux de victoire en fonction du temps d'exécution")
plt.xlabel("Temps (ms)")
plt.ylabel("Taux de victoire")
plt.tight_layout()
plt.show()

# Heatmap taux de victoire : SimDepth vs Budget
pivot = df_strict.pivot_table(index='SimDepth', columns='Budget', values='WinRate')
plt.figure(figsize=(8, 6))
sns.heatmap(pivot, annot=True, fmt=".2f", cmap="YlGnBu")
plt.title("Taux de victoire selon Budget et SimDepth")
plt.xlabel("Budget")
plt.ylabel("Profondeur de simulation (SimDepth)")
plt.tight_layout()
plt.show()

# Taux de victoire en fonction du nombre de simulations réelles
plt.figure(figsize=(10, 6))
sns.lineplot(data=df_strict, x='Simulations', y='WinRate', hue='SimDepth', marker='o')
plt.title("Taux de victoire en fonction du nombre de simulations réelles")
plt.xlabel("Nombre de simulations")
plt.ylabel("Taux de victoire")
plt.ylim(0, 1)
plt.tight_layout()
plt.show()
