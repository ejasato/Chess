package chess.model.checkmate;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

import java.util.List;

public class MoveSimulator {

    private final Board board;

    public MoveSimulator(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isLegalMove(Piece piece, int toCol, int toRow) {

        if (piece == null) return false;
        if (!board.isInside(toCol, toRow)) return false;

        if (!piece.canMove(toCol, toRow, board)) {
            return false;
        }

        Move move = new Move(piece.col, piece.row, toCol, toRow);

        board.makeMove(move);
        boolean inCheck = isKingInCheck(piece.getColor());
        board.undoMove(move);

        return !inCheck;
    }

    public boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        int enemy = opposite(color);

        for (Piece p : board.getAllPieces()) {
            if (p.getColor() != enemy) continue;
            if (p.canMove(king.col, king.row, board)) {
                return true;
            }
        }
        return false;
    }

    private Piece findKing(int color) {
        for (Piece p : board.getAllPieces()) {
            if (p.getColor() == color && p.getType() == Type.KING) {
                return p;
            }
        }
        return null;
    }

    private int opposite(int c) {
        return (c == 0 ? 1 : 0);
    }

    public boolean isValidKingMove(Piece king, int dCol, int dRow) {

        int newCol = king.col + dCol;
        int newRow = king.row + dRow;

        if (!board.isInside(newCol, newRow)) return false;
        if (!king.canMove(newCol, newRow, board)) return false;

        Move m = new Move(king.col, king.row, newCol, newRow);
        board.makeMove(m);
        boolean inCheck = isKingInCheck(king.getColor());
        board.undoMove(m);

        return !inCheck;
    }
}

