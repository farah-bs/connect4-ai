import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minimax {
    public static int nodesExplored = 0; // compteur global

    public int getBestMove(GameState state, int maxDepth) {
        List<Integer> bestMoves = new ArrayList<>();
        int bestValue = Integer.MIN_VALUE;
        nodesExplored = 0; // reset counter

        for (int col = 1; col <= 7; col++) {
            GameState copy = copyState(state);
            if (!copy.move(col)) continue;

            int value = joueurMin(copy, maxDepth - 1);
            if (value > bestValue) {
                bestValue = value;
                bestMoves.clear();
                bestMoves.add(col);
            } else if (value == bestValue) {
                bestMoves.add(col);
            }
        }

        if (bestMoves.isEmpty()) return -1;
        int chosenMove = bestMoves.get(new Random().nextInt(bestMoves.size()));

        //System.out.println("Meilleur coup pour " + (state.getRedsTurn() ? "Rouge" : "Jaune") + ": Colonne " + chosenMove + " avec score " + bestValue);
        return chosenMove;
    }

    private int joueurMax(GameState state, int depth) {
        nodesExplored++;

        if (state.getGameOver() || depth == 0)
            return Evaluation.evaluate(state);

        int best = Integer.MIN_VALUE;
        for (int col = 1; col <= 7; col++) {
            GameState copy = copyState(state);
            if (!copy.move(col)) continue;

            best = Math.max(best, joueurMin(copy, depth - 1));
        }
        return best;
    }

    private int joueurMin(GameState state, int depth) {
        nodesExplored++;

        if (state.getGameOver() || depth == 0)
            return Evaluation.evaluate(state);

        int best = Integer.MAX_VALUE;
        for (int col = 1; col <= 7; col++) {
            GameState copy = copyState(state);
            if (!copy.move(col)) continue;

            best = Math.min(best, joueurMax(copy, depth - 1));
        }
        return best;
    }

    private GameState copyState(GameState state) {
        GameState copy = new GameState();
        for (Point p : state.getMoves()) {
            copy.move((int)p.getX() + 1);
        }
        return copy;
    }
}
