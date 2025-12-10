//package chess.model.move;
//
//import chess.model.pieces.Piece;
//import chess.model.pieces.Type;
//
//public class Move {
//
//    public final int fromCol;
//    public final int fromRow;
//    public final int toCol;
//    public final int toRow;
//
//    public Piece moved;
//    public Piece captured;
//
//    // ===== Promotion =====
//    public boolean isPromotion = false;
//    public Type promotionType = null;
//
//    // ===== En Passant =====
//    public boolean isEnPassant = false;
//    public int capturedCol; // where the pawn actually was
//    public int capturedRow;
//
//    // ===== Castling =====
//    public boolean isCastle = false;
//    public int rookFromCol;
//    public int rookFromRow;
//    public int rookToCol;
//    public int rookToRow;
//
//    public Move(int fromCol, int fromRow, int toCol, int toRow) {
//        this.fromCol = fromCol;
//        this.fromRow = fromRow;
//        this.toCol = toCol;
//        this.toRow = toRow;
//    }
//
//    @Override
//    public String toString() {
//        char fromFile = (char) ('a' + fromCol);
//        int fromRank = 8 - fromRow;
//        char toFile = (char) ('a' + toCol);
//        int toRank = 8 - toRow;
//        return "" + fromFile + fromRank + "-" + toFile + toRank;
//    }
//}


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

    // NEW FLAGS
    public boolean isEnPassant = false;
    public boolean isCastle = false;

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
