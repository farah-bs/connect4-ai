public class Evaluation {

    public static int evaluate(GameState state) {
        Boolean[][] board = state.getPieces();
        int scoreRed = getScore(board, true);
        int scoreYellow = getScore(board, false);
        return state.getRedsTurn() ? scoreRed - scoreYellow : scoreYellow - scoreRed;
    }

    private static int getScore(Boolean[][] board, boolean isRed) {
        int score = 0;
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}}; // H, V, Diag ↘, Diag ↗

        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                for (int[] dir : directions) {
                    score += evalWindow(board, x, y, dir[0], dir[1], isRed);
                }
            }
        }

        return score;
    }

    private static int evalWindow(Boolean[][] board, int x, int y, int dx, int dy, boolean isRed) {
        int count = 0;
        int empty = 0;

        for (int i = 0; i < 4; i++) {
            int cx = x + dx * i;
            int cy = y + dy * i;
            if (cx < 0 || cx >= 7 || cy < 0 || cy >= 6) return 0;
            Boolean val = board[cx][cy];
            if (val == null) empty++;
            else if (val == isRed) count++;
            else return 0; // pion adverse
        }

        if (count == 4) return 1000;
        if (count == 3 && empty == 1) return 50;
        if (count == 2 && empty == 2) return 5;
        if (count == 1 && empty == 3) return 1;
        return 0;
    }
}
