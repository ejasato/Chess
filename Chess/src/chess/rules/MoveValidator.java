package chess.rules;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.*;
import chess.model.pieces.GamePanel;

public class MoveValidator {

    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    /** -----------------------------------------------------------
     *  Checks if king of given color is in check
     * ----------------------------------------------------------- */
    public boolean isKingInCheck(int color) {

        Piece king = findKing(color);
        if (king == null) return false;

        int enemy = (color == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);

        // Loop through enemy pieces
        for (Piece p : GamePanel.simPieces) {

            if (p.getColor() != enemy) continue;

            // Updated to use board argument
            if (p.canMove(king.col, king.row, board)) {
                return true;
            }
        }

        return false;
    }

    /** -----------------------------------------------------------
     *  Find the king
     * ----------------------------------------------------------- */
    private Piece findKing(int color) {
        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() == color && p.getType() == Type.KING) {
                return p;
            }
        }
        return null;
    }

    /** -----------------------------------------------------------
     *  Checks if a move is legal:
     *  - must not leave king in check
     * ----------------------------------------------------------- */
    public boolean isLegalMove(Move move, int color) {

        board.makeMove(move);     // simulate
        boolean inCheck = isKingInCheck(color);
        board.undoMove(move);     // revert

        return !inCheck;
    }
}
