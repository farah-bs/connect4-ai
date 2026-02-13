import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;

public class BoardDrawing  extends JComponent  {
    private GameState state;
    private Rectangle board;
    private Rectangle textBack;
    private String stateMessage;
    private ArrayList<Ellipse2D.Double> holes;

    private Minimax minimax = new Minimax();
    private AlphaBeta alphabeta = new AlphaBeta();
    private MCTS mcts = new MCTS();

    private TypeIA typeIaRed;
    private TypeIA typeIaYellow;
    private ModeDeJeu mode;

    enum ModeDeJeu {
        JOUEUR_VS_JOUEUR,
        JOUEUR_VS_IA,
        IA_VS_IA
    }

    enum TypeIA {
        JOUEUR,
        MINIMAX,
        ALPHABETA,
        MCTS
    }

    final int BOARD_START_X = 182;
    final int BOARD_START_Y = 75;
    final int BOARD_WIDTH = 386;
    final int BOARD_HEIGHT = 340;
    final int HOLE_DIAMETER = 36;
    final int HOLE_DISTANCE = 50;
    final int HOLE_OFFSET = 25;
    final int HOLE_START_X = BOARD_START_X + HOLE_OFFSET;
    final int HOLE_START_Y = BOARD_START_Y + BOARD_HEIGHT - HOLE_OFFSET - HOLE_DIAMETER;

    public BoardDrawing(GameState gs) {
        this.state = gs;

        Object[] options = {"Human", "Minimax", "AlphaBeta", "MCTS"};
        int choixRed = JOptionPane.showOptionDialog(this, "Pick a player for RED ?", "Player 1",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        int choixYellow = JOptionPane.showOptionDialog(this, "Pick a player for YELLOW ?", "Player 2",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        typeIaRed = TypeIA.values()[choixRed];
        typeIaYellow = TypeIA.values()[choixYellow];

        if (typeIaRed != TypeIA.JOUEUR && typeIaYellow != TypeIA.JOUEUR) {
            mode = ModeDeJeu.IA_VS_IA;
            lancerIAvsIA();
        } else if (typeIaRed == TypeIA.JOUEUR && typeIaYellow == TypeIA.JOUEUR) {
            mode = ModeDeJeu.JOUEUR_VS_JOUEUR;
        } else {
            mode = ModeDeJeu.JOUEUR_VS_IA;
        }

        board = new Rectangle(BOARD_START_X, BOARD_START_Y, BOARD_WIDTH, BOARD_HEIGHT);
        textBack = new Rectangle(BOARD_START_X + (BOARD_WIDTH / 2) - 100, BOARD_START_Y - 50, 200, 40);
        holes = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                holes.add(new Ellipse2D.Double(HOLE_START_X + i * HOLE_DISTANCE,
                        HOLE_START_Y - j * HOLE_DISTANCE, HOLE_DIAMETER, HOLE_DIAMETER));
            }
        }

        if (mode != ModeDeJeu.IA_VS_IA) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (state.getGameOver()) return;

                    int mouseX = e.getX();
                    if (mouseX < HOLE_START_X || mouseX > HOLE_START_X + 6 * HOLE_DISTANCE + HOLE_DIAMETER) return;

                    int col = (mouseX - HOLE_START_X) / HOLE_DISTANCE;

                    if (!state.move(col + 1)) {
                        JOptionPane.showMessageDialog(BoardDrawing.this, "Colonne pleine", "Erreur", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    repaint();

                    if (!state.getGameOver()) {
                        jouerIA();
                    }
                }
            });
        }
    }

    private void jouerIA() {
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}

            if (state.getGameOver()) {
                SwingUtilities.invokeLater(this::repaint);
                return;
            }

            boolean redTurn = state.getRedsTurn();
            TypeIA currentIA = redTurn ? typeIaRed : typeIaYellow;
            int move = -1;

            switch (currentIA) {
                case MINIMAX:
                    move = minimax.getBestMove(state.clone(), 6);
                    break;
                case ALPHABETA:
                    move = alphabeta.getBestMove(state.clone(), 6);
                    break;
                case MCTS:
                    move = mcts.getBestMove(state.clone(), 1000);
                    break;
                case JOUEUR:
                    return;
            }

            if (move != -1) {
                state.move(move);
            }

            SwingUtilities.invokeLater(() -> {
                repaint();
                if (mode == ModeDeJeu.IA_VS_IA && !state.getGameOver()) {
                    jouerIA();
                }
            });
        }).start();

    }

    private void lancerIAvsIA() {
        jouerIA();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("TimesRoman", Font.BOLD, 20));

        g2.setColor(Color.blue);
        g2.fill(board);

        g2.setColor(Color.white);
        for (Ellipse2D.Double hole : holes) g2.fill(hole);

        g2.setColor(Color.black);
        for (int i = 0; i < 7; i++) {
            int w = g2.getFontMetrics().stringWidth(Integer.toString(i + 1));
            g2.drawString(Integer.toString(i + 1),
                    HOLE_START_X + HOLE_DIAMETER / 2 - w / 2 + i * HOLE_DISTANCE,
                    BOARD_START_Y + BOARD_HEIGHT + 25);
        }

        if (state.getRedWins()) {
            g2.setColor(Color.red);
            g2.fill(textBack);
            g.setColor(Color.black);
            stateMessage = "RED WINS!";
        } else if (state.getYellowWins()) {
            g2.setColor(Color.yellow);
            g2.fill(textBack);
            g.setColor(Color.black);
            stateMessage = "YELLOW WINS!";
        } else if (state.getGameOver()) {
            g2.setColor(Color.black);
            g2.fill(textBack);
            g.setColor(Color.white);
            stateMessage = "IT'S A TIE!";
        } else if (state.getRedsTurn()) {
            g2.setColor(Color.lightGray);
            g2.fill(textBack);
            g.setColor(Color.red);
            stateMessage = "Red's Turn";
        } else {
            g2.setColor(Color.lightGray);
            g2.fill(textBack);
            g.setColor(Color.yellow);
            stateMessage = "Yellow's Turn";
        }

        int stringWidth = g2.getFontMetrics().stringWidth(stateMessage);
        int strX = BOARD_START_X + (BOARD_WIDTH - stringWidth) / 2;
        g2.drawString(stateMessage, strX, BOARD_START_Y - 22);

        if (state.getError() != null) {
            g2.setColor(Color.black);
            g2.drawString("Oops!", 15, 220);
            g2.drawString(state.getError(), 15, 250);
        }

        for (int i = 0; i < state.getPieces().length; i++) {
            for (int j = 0; j < state.getPieces()[0].length; j++) {
                if (state.getPieces()[i][j] == null) continue;
                g2.setColor(state.getPieces()[i][j] ? Color.red : Color.yellow);
                g2.fill(new Ellipse2D.Double(
                        HOLE_START_X + 2 + i * HOLE_DISTANCE,
                        HOLE_START_Y + 2 - j * HOLE_DISTANCE,
                        HOLE_DIAMETER - 4,
                        HOLE_DIAMETER - 4));
            }
        }
    }

    public boolean jouerCoupUtilisateur(int colonne) {
        if (state.getGameOver()) return false;

        boolean success = state.move(colonne);
        repaint();

        if (success && !state.getGameOver()) {
            jouerIA();
        }
        return success;
    }

    public boolean undoDernierCoup() {
        boolean success = state.undo();
        repaint();
        return success;
    }

}
