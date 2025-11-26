package chess.engine.movegen;

import chess.model.board.Board;
import chess.model.pieces.Piece;
import chess.model.move.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates pseudo-legal moves by asking each piece for its canMove(target).
 * Does NOT check for leaving the king in check (that's OK for now).
 */
public class MoveGenerator {
    private final Board board;

    public MoveGenerator(Board board) {
        this.board = board;
    }

    public List<Move> generateMoves(int color) {
        List<Move> moves = new ArrayList<>(128);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(c, r);
                if (p == null) continue;
                if (p.getColor() != color) continue;

                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (p.canMove(tc, tr)) {
                            moves.add(new Move(c, r, tc, tr));
                        }
                    }
                }
            }
        }

        return moves;
    }
}