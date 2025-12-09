package chess.model.move;

import chess.model.pieces.Piece;
import chess.model.pieces.Type;

public class Move {

    public final int fromCol;
    public final int fromRow;
    public final int toCol;
    public final int toRow;

    public Piece moved;
    public Piece captured;

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