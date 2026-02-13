import java.util.*;
import java.awt.Point;

public class MCTS {
    private static final double C = Math.sqrt(2);
    private double explorationConstant = Math.sqrt(2);

    // Champs pour statistiques
    private int lastSimulations = 0;
    private int lastBestMove = -1;
    private double lastBestScore = 0.0;

    public void setExplorationConstant(double c) {
        this.explorationConstant = c;
    }

    public int getBestMoveWithCustomSimDepth(GameState rootState, int budget, int simDepth) {
        Node root = new Node(rootState, null, -1);

        for (int i = 0; i < budget; i++) {
            Node v = treePolicy(root);
            double delta = defaultPolicy(v.state, simDepth);
            backup(v, delta);
        }

        TreeStats stats = new TreeStats();
        stats.analyze(root);
        stats.print();

        Node best = bestChild(root, 0);

        // Enregistrement des statistiques
        lastSimulations = budget;
        lastBestMove = best.move;
        lastBestScore = best.totalValue / best.visits;

        TestComparatifIA.MCTSNodeCounter.setLastSimCount(budget);

        return best.move;
    }

    public int getSimulationCount() {
        return lastSimulations;
    }

    public int getLastBestMove() {
        return lastBestMove;
    }

    public double getEstimatedWinRate() {
        return lastBestScore;
    }

    public int getBestMove(GameState rootState, int budget) {
        return getBestMoveWithCustomSimDepth(rootState, budget, 10); // par défaut profondeur 10
    }

    private Node treePolicy(Node v) {
        while (!v.state.getGameOver()) {
            if (!v.isFullyExpanded()) {
                return expand(v);
            } else {
                v = bestChild(v, explorationConstant);
            }
        }
        return v;
    }

    private Node expand(Node v) {
        for (int move : v.untriedMoves) {
            GameState nextState = cloneState(v.state);
            if (nextState.move(move)) {
                Node child = new Node(nextState, v, move);
                v.children.add(child);
                v.untriedMoves.remove((Integer) move);
                return child;
            }
        }
        return null;
    }

    private Node bestChild(Node v, double c) {
        Node best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Node child : v.children) {
            double exploitation = child.totalValue / child.visits;
            double exploration = c * Math.sqrt(2 * Math.log(v.visits) / child.visits);
            double uct = exploitation + exploration;

            if (uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        return best;
    }

    private double defaultPolicy(GameState s, int maxDepth) {
        GameState sim = cloneState(s);
        int depth = 0;

        while (!sim.getGameOver() && depth < maxDepth) {
            List<Integer> actions = getAvailableMoves(sim);
            int a = actions.get(new Random().nextInt(actions.size()));
            sim.move(a);
            depth++;
        }

        if (sim.isDraw()) return 0.5;
        if (sim.getRedsTurn()) return sim.getYellowWins() ? 1.0 : 0.0;
        else return sim.getRedWins() ? 1.0 : 0.0;
    }

    private void backup(Node v, double delta) {
        while (v != null) {
            v.visits += 1;
            v.totalValue += delta;
            v = v.parent;
        }
    }

    private List<Integer> getAvailableMoves(GameState state) {
        List<Integer> moves = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            GameState temp = cloneState(state);
            if (temp.move(i)) {
                moves.add(i);
            }
        }
        return moves;
    }

    private GameState cloneState(GameState state) {
        GameState newState = new GameState();
        for (Point move : state.getMoves()) {
            newState.move(move.x + 1);
        }
        return newState;
    }

    public static class Node {
        GameState state;
        Node parent;
        List<Node> children = new ArrayList<>();
        List<Integer> untriedMoves;
        int move;
        int visits = 0;
        double totalValue = 0.0;

        Node(GameState state, Node parent, int move) {
            this.state = state;
            this.parent = parent;
            this.move = move;
            this.untriedMoves = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                GameState temp = new GameState();
                for (Point p : state.getMoves()) temp.move(p.x + 1);
                if (temp.move(i)) untriedMoves.add(i);
            }
        }

        boolean isFullyExpanded() {
            return untriedMoves.isEmpty();
        }
    }
}
