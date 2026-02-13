import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestIAProfondeurMulti {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        int nbParties = 5;
        int[] profondeurs = {2, 4, 6, 8};
        String outputPath = "resultats_ia.csv";

        // Initialiser le fichier avec l'en-tête CSV
        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write("ProfMinimax,ProfAlphaBeta,VictoireMinimax,VictoireAlphaBeta,MatchNul,TempsMinimax(ms),TempsAlphaBeta(ms),NoeudsMinimax,NoeudsAlphaBeta\n");
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int depthMinimax : profondeurs) {
            for (int depthAlphaBeta : profondeurs) {
                int finalDepthMinimax = depthMinimax;
                int finalDepthAlphaBeta = depthAlphaBeta;

                tasks.add(() -> {
                    int redWins = 0, yellowWins = 0, draws = 0;
                    long totalTimeRed = 0, totalTimeYellow = 0;
                    long totalNodesRed = 0, totalNodesYellow = 0;

                    for (int i = 0; i < nbParties; i++) {
                        GameState state = new GameState();
                        Minimax minimax = new Minimax();
                        AlphaBeta alphabeta = new AlphaBeta();
                        boolean redTurn = true;

                        while (!state.getGameOver()) {
                            long startTime = System.nanoTime();
                            int move;

                            if (redTurn) {
                                Minimax.nodesExplored = 0;
                                move = minimax.getBestMove(state.clone(), finalDepthMinimax);
                                totalNodesRed += Minimax.nodesExplored;
                            } else {
                                AlphaBeta.nodesExplored = 0;
                                move = alphabeta.getBestMove(state.clone(), finalDepthAlphaBeta);
                                totalNodesYellow += AlphaBeta.nodesExplored;
                            }

                            long duration = System.nanoTime() - startTime;
                            if (redTurn) totalTimeRed += duration;
                            else totalTimeYellow += duration;

                            if (!state.move(move)) break;
                            redTurn = !redTurn;
                        }

                        if (state.getRedWins()) redWins++;
                        else if (state.getYellowWins()) yellowWins++;
                        else draws++;
                    }

                    double avgTimeRed = totalTimeRed / 1_000_000.0 / nbParties;
                    double avgTimeYellow = totalTimeYellow / 1_000_000.0 / nbParties;
                    long avgNodesRed = totalNodesRed / nbParties;
                    long avgNodesYellow = totalNodesYellow / nbParties;

                    synchronized (TestIAProfondeurMulti.class) {
                        try (FileWriter fw = new FileWriter(outputPath, true)) {
                            fw.write(finalDepthMinimax + "," + finalDepthAlphaBeta + "," +
                                    redWins + "," + yellowWins + "," + draws + "," +
                                    String.format("%.2f", avgTimeRed) + "," +
                                    String.format("%.2f", avgTimeYellow) + "," +
                                    avgNodesRed + "," + avgNodesYellow + "\n");
                        }
                    }

                    return null;
                });
            }
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get(); // ensure all are completed
        }

        executor.shutdown();
        System.out.println("✅ Résultats enregistrés dans le fichier : " + outputPath);
    }
}
