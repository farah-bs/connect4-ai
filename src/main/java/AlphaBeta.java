import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class AlphaBeta {
    public static int nodesExplored = 0; // compteur global

//    public static int getBestMove(GameState state, int maxProfondeur) {
//        int bestAction = -1;
//        int bestEval = Integer.MIN_VALUE;
//        int alpha = Integer.MIN_VALUE;
//        int beta = Integer.MAX_VALUE;
//        nodesExplored = 0; // reset counter
//
//
//        for (int col = 1; col <= 7; col++) {
//            if (state.move(col)) {
//                int eval = joueurMin(state, maxProfondeur - 1, alpha, beta);
//                state.undo();
//
//                if (eval > bestEval) {
//                    bestEval = eval;
//                    bestAction = col;
//                }
//                alpha = Math.max(alpha, bestEval);
//            }
//        }
//
//        return bestAction;
//    }

    public static int getBestMove(GameState state, int maxProfondeur) {
        List<Integer> bestMoves = new ArrayList<>();
        int bestEval = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        nodesExplored = 0;

        for (int col = 1; col <= 7; col++) {
            if (state.move(col)) {
                int eval = joueurMin(state, maxProfondeur - 1, alpha, beta);
                state.undo();

                if (eval > bestEval) {
                    bestEval = eval;
                    bestMoves.clear();
                    bestMoves.add(col);
                } else if (eval == bestEval) {
                    bestMoves.add(col);
                }
                alpha = Math.max(alpha, bestEval);
            }
        }

        if (bestMoves.isEmpty()) return -1;
        return bestMoves.get(new Random().nextInt(bestMoves.size()));
    }


    private static int joueurMax(GameState state, int profondeur, int alpha, int beta) {
        nodesExplored++;
        if (state.getGameOver() || profondeur == 0) {
            return Evaluation.evaluate(state);
        }

        int u = Integer.MIN_VALUE;

        for (int col = 1; col <= 7; col++) {
            if (state.move(col)) {
                int eval = joueurMin(state, profondeur - 1, alpha, beta);
                state.undo();

                if (eval > u) {
                    u = eval;
                }
                alpha = Math.max(alpha, u);
                if (beta <= alpha) {
                    break; // Coupe alpha-beta
                }
            }
        }

        return u;
    }

    private static int joueurMin(GameState state, int profondeur, int alpha, int beta) {
        nodesExplored++;
        if (state.getGameOver() || profondeur == 0) {
            return Evaluation.evaluate(state);
        }

        int u = Integer.MAX_VALUE;

        for (int col = 1; col <= 7; col++) {
            if (state.move(col)) {
                int eval = joueurMax(state, profondeur - 1, alpha, beta);
                state.undo();

                if (eval < u) {
                    u = eval;
                }
                beta = Math.min(beta, u);
                if (beta <= alpha) {
                    break; // Coupe alpha-beta
                }
            }
        }

        return u;
    }
}
