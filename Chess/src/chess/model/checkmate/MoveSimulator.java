package chess.model.checkmate;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.GamePanel;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

public class MoveSimulator {

    private final Board board;

    public MoveSimulator(Board board) {
        this.board = board;
    }

    // Expose board so CheckMateDetector can use it
    public Board getBoard() {
        return board;
    }

    /**
     * Test if moving `piece` to (toCol,toRow) is legal.
     */
    public boolean isLegalMove(Piece piece, int toCol, int toRow) {

        // 1. First check if the piece is allowed to move there by its own rules.
        if (!piece.canMove(toCol, toRow, board)) {
            return false;
        }

        // 2. Build a move object.
        Move move = new Move(piece.col, piece.row, toCol, toRow);

        // 3. Apply move on board (temporary)
        board.makeMove(move);

        // 4. Check king safety
        boolean isIllegal = kingIsInCheck(piece.getColor());  // true means illegal

        // 5. Undo move
        board.undoMove(move);

        return !isIllegal;
    }

    /**
     * Tests if the king of `color` is currently in check.
     */
    private boolean kingIsInCheck(int color) {

        Piece king = findKing(color);
        if (king == null) return false;

        // Scan all enemy pieces that can attack the king
        for (Piece enemy : GamePanel.simPieces) {
            if (enemy.getColor() != color) {
                if (enemy.canMove(king.col, king.row, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the king of a given color using simPieces.
     */
    private Piece findKing(int color) {
        for (Piece p : GamePanel.simPieces) {
            if (p.getType() == Type.KING && p.getColor() == color) {
                return p;
            }
        }
        return null;
    }

    /**
     * Determines if a king is allowed to move by (dCol, dRow).
     * Used for king-move validation in check/checkmate.
     */
    public boolean isValidKingMove(Piece king, int dCol, int dRow) {

        int newCol = king.col + dCol;
        int newRow = king.row + dRow;

        // Raw movement rule check
        if (!king.canMove(newCol, newRow, board)) {
            return false;
        }

        // Simulate the move
        Move move = new Move(king.col, king.row, newCol, newRow);
        board.makeMove(move);

        boolean illegal = kingIsInCheck(king.getColor());

        board.undoMove(move);

        return !illegal;
    }
}


