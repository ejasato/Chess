package chess.model.move;

import chess.model.pieces.Piece;
import chess.model.pieces.Type;

/**
 * Represents a single chess move.
 * Stores from/to squares and enough info to undo the move (moved + captured).
 */
public class Move {

    public final int fromCol;
    public final int fromRow;
    public final int toCol;
    public final int toRow;

    // Will be set by Board.makeMove(...)
    public Piece moved;
    public Piece captured;

    // Optional: promotion support
    public boolean isPromotion = false;
    public Type promotionType = null;

    public Move(int fromCol, int fromRow, int toCol, int toRow) {
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
    }

    @Override
    public String toString() {
        char fromFile = (char) ('a' + fromCol);
        int fromRank = 8 - fromRow;
        char toFile = (char) ('a' + toCol);
        int toRank = 8 - toRow;

        return "" + fromFile + fromRank + "-" + toFile + toRank;
    }
}