import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class TestComparatifIA {

    public static void main(String[] args) throws Exception {
        int nbParties = 5;

        int[] profondeurs = {2, 4, 6};
        int[] budgetsMCTS = {100, 500, 1000, 5000};
        int simDepth = 6;

        String[] algos = {"Minimax", "AlphaBeta", "MCTS"};

        PrintWriter writer = new PrintWriter(new FileWriter("resultats_ia.csv"));
        writer.println("AlgoRouge,ParamRouge,AlgoJaune,ParamJaune,Vainqueur,TempsRouge(ms),TempsJaune(ms),NoeudsRouge,NoeudsJaune");

        for (String algoRed : algos) {
            for (String algoYellow : algos) {
                for (int paramRed : (algoRed.equals("MCTS") ? budgetsMCTS : profondeurs)) {
                    for (int paramYellow : (algoYellow.equals("MCTS") ? budgetsMCTS : profondeurs)) {

                        for (int i = 0; i < nbParties; i++) {
                            GameState state = new GameState();

                            long tempsRouge = 0, tempsJaune = 0;
                            long noeudsRouge = 0, noeudsJaune = 0;

                            while (!state.getGameOver()) {
                                int move;
                                long start = System.currentTimeMillis();

                                if (state.getRedsTurn()) {
                                    move = getBestMove(algoRed, paramRed, simDepth, state);
                                    tempsRouge += (System.currentTimeMillis() - start);
                                    noeudsRouge += getNodeCount(algoRed);
                                } else {
                                    move = getBestMove(algoYellow, paramYellow, simDepth, state);
                                    tempsJaune += (System.currentTimeMillis() - start);
                                    noeudsJaune += getNodeCount(algoYellow);
                                }

                                state.move(move);
                            }

                            String vainqueur = state.getRedWins() ? "Rouge" : state.getYellowWins() ? "Jaune" : "Nul";

                            writer.printf(Locale.US,
                                    "%s,%d,%s,%d,%s,%d,%d,%d,%d\n",
                                    algoRed, paramRed, algoYellow, paramYellow,
                                    vainqueur,
                                    tempsRouge, tempsJaune,
                                    noeudsRouge, noeudsJaune
                            );
                            writer.flush();

                            System.out.printf("Partie %d : %s(%d) vs %s(%d) -> %s\n",
                                    i + 1, algoRed, paramRed, algoYellow, paramYellow, vainqueur);
                        }
                    }
                }
            }
        }

        writer.close();
        System.out.println("Tous les tests sont terminés.");
    }

    public static int getBestMove(String algo, int param, int simDepth, GameState state) {
        return switch (algo) {
            case "Minimax" -> {
                Minimax.nodesExplored = 0;
                yield new Minimax().getBestMove(state, param);
            }
            case "AlphaBeta" -> {
                AlphaBeta.nodesExplored = 0;
                yield AlphaBeta.getBestMove(state, param);
            }
            case "MCTS" -> {
                MCTS mcts = new MCTS();
                yield mcts.getBestMoveWithCustomSimDepth(state, param, simDepth);
            }
            default -> throw new IllegalArgumentException("Algorithme inconnu : " + algo);
        };
    }

    public static long getNodeCount(String algo) {
        return switch (algo) {
            case "Minimax" -> Minimax.nodesExplored;
            case "AlphaBeta" -> AlphaBeta.nodesExplored;
            case "MCTS" -> MCTSNodeCounter.getLastSimCount();
            default -> 0;
        };
    }

    // Classe statique utilitaire pour récupérer le budget MCTS
    static class MCTSNodeCounter {
        private static int lastSimCount = 0;
        public static int getLastSimCount() {
            return lastSimCount;
        }
        public static void setLastSimCount(int count) {
            lastSimCount = count;
        }
    }
}
