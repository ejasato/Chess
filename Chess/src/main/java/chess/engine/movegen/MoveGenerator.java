package chess.engine.movegen;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.Piece;
import chess.model.pieces.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    private final Board board;

    public MoveGenerator(Board board) {
        this.board = board;
    }

    public List<Move> generateMoves(int color) {
        List<Move> moves = new ArrayList<>();

        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() != color) continue;

            int fromCol = p.col;
            int fromRow = p.row;

            // try every target square on the board
            for (int tc = 0; tc < 8; tc++) {
                for (int tr = 0; tr < 8; tr++) {

                    // skip same square
                    if (tc == fromCol && tr == fromRow) continue;

                    // use piece’s canMove **with board**
                    if (p.canMove(tc, tr, board)) {
                        Move m = new Move(fromCol, fromRow, tc, tr);
                        moves.add(m);
                    }
                }
            }
        }

        return moves;
    }
}
