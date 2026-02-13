import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestMCTSStats {
    public static void main(String[] args) {
        int[] budgets = {500, 1000, 5000};
        int[] simulationDepths = {5, 10, 20};
        double[] explorationConstants = {0.5, Math.sqrt(2), 3.0};

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultats_mcts.csv"))) {
            writer.println("Budget,SimDepth,C,SelectedMove,Simulations,WinRate,Time(ms)");

            for (int budget : budgets) {
                for (int depthLimit : simulationDepths) {
                    for (double C : explorationConstants) {
                        GameState state = new GameState();
                        MCTS mcts = new MCTS();
                        mcts.setExplorationConstant(C);

                        long startTime = System.currentTimeMillis();
                        int selectedMove = mcts.getBestMoveWithCustomSimDepth(state, budget, depthLimit);
                        long endTime = System.currentTimeMillis();

                        int simulations = mcts.getSimulationCount();
                        double winRate = mcts.getEstimatedWinRate();

                        writer.printf("%d;%d;%.3f;%d;%d;%.4f;%d%n",
                                budget, depthLimit, C, selectedMove, simulations, winRate, (endTime - startTime));

                    }
                }
            }

            System.out.println("Résultats sauvegardés dans resultats_mcts.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
