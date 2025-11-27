package chess.engine.eval;

import chess.model.board.Board;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;
import chess.model.pieces.GamePanel;

/**
 * Small, fast static evaluator. Sums material + simple piece-square bonuses.
 * Positive = advantage for WHITE, Negative = advantage for BLACK.
 */
public class Evaluator {
	
	//The amount of points these pieces are worth coming from Hans Berliner system
    private static final int PAWN = 100;
    private static final int KNIGHT = 320;
    private static final int BISHOP = 330;
    private static final int ROOK = 500;
    private static final int QUEEN = 900;
    private static final int KING = 20000;

    //evaluate the advantage between each player or chess engine by finding the difference between each players points associated with pieces
    public int evaluate(Board board) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(c, r);
                if (p == null) continue;
                int v = valueOf(p.getType());
                int sign = (p.getColor() == GamePanel.WHITE) ? 1 : -1;
                score += sign * v;
            }
        }
        return score;
    }
    //this turns the piece into a number corresponding to their points
    private int valueOf(Type t) {
        switch (t) {
            case PAWN: return PAWN;
            case KNIGHT: return KNIGHT;
            case BISHOP: return BISHOP;
            case ROOK: return ROOK;
            case QUEEN: return QUEEN;
            case KING: return KING;
            default: return 0;
        }
    }
}