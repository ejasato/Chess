import chess.model.board.Board;
import chess.model.pieces.GamePanel;
import chess.engine.search.Engine;
import chess.model.move.Move;

public class TestEngine {

    public static void main(String[] args) {

        Board board = new Board();
        board.setupStartingPosition();   // load full chess position

        System.out.println("Initial Position:");
        board.printBoard();

        Engine engine = new Engine(board);

        // WHITE = 0, BLACK = 1 (as defined in GamePanel)
        int color = GamePanel.WHITE;

        System.out.println("Engine thinking...");
        Move best = engine.findBestMove(color, 3);  // search depth=3

        if (best == null) {
            System.out.println("Engine found no moves.");
        } else {
            System.out.println("Engine recommends: " + best);

            System.out.println("\nApplying move...");
            board.makeMove(best);
            board.printBoard();
        }
    }
}