package chess.rules;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.GamePanel;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

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

            // Use board-aware canMove
            if (p.canMove(king.col, king.row, board)) {
                return true;
            }
        }

        return false;
    }

    /** -----------------------------------------------------------
     *  Find the king of a color
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
     *  - piece must exist & be correct color
     *  - must obey piece movement rules
     *  - must not leave own king in check
     * ----------------------------------------------------------- */
    public boolean isLegalMove(Move move, int color) {

        Piece moving = board.getPiece(move.fromCol, move.fromRow);
        if (moving == null) {
            return false;
        }

        // wrong color
        if (moving.getColor() != color) {
            return false;
        }

        // obey piece move rules first
        if (!moving.canMove(move.toCol, move.toRow, board)) {
            return false;
        }

        // simulate
        board.makeMove(move);
        boolean inCheck = isKingInCheck(color);
        board.undoMove(move);

        return !inCheck;
    }
}

