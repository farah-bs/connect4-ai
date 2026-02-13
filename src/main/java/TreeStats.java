import java.util.*; // suffisant ici

public class TreeStats {
    int maxDepth = 0;
    int totalNodes = 0;
    Map<Integer, Integer> nodesPerLevel = new HashMap<>();

    // Prend en paramètre un MCTS.Node
    public void analyze(MCTS.Node root) {
        dfs(root, 0);
    }

    private void dfs(MCTS.Node node, int depth) {
        maxDepth = Math.max(maxDepth, depth);
        totalNodes++;
        nodesPerLevel.put(depth, nodesPerLevel.getOrDefault(depth, 0) + 1);

        for (MCTS.Node child : node.children) {
            dfs(child, depth + 1);
        }
    }

    public void print() {
        System.out.println("Nombre total de noeuds : " + totalNodes);
        System.out.println("Profondeur maximale : " + maxDepth);
        System.out.println("Répartition des noeuds par niveau :");
        for (Map.Entry<Integer, Integer> e : nodesPerLevel.entrySet()) {
            System.out.println("  Niveau " + e.getKey() + " : " + e.getValue() + " noeuds");
        }
    }


}
