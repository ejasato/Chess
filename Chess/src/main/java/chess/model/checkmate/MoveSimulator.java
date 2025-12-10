package chess.model.checkmate;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

import java.util.List;
import java.util.ArrayList;


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
    
    public boolean isSquareAttacked(int col, int row, int byColor) {

        for (Piece p : board.getAllPieces()) {
            if (p.getColor() != byColor) continue;

            if (attacksSquare(p, col, row)) {
                return true;
            }
        }

        return false;
    }
    
    private boolean attacksSquare(Piece p, int targetCol, int targetRow) {

        int pc = p.col;
        int pr = p.row;

        switch (p.getType()) {

            case PAWN:
                int dir = (p.getColor() == 0 ? -1 : 1); // white up, black down
                return (targetCol == pc - 1 || targetCol == pc + 1) &&
                       targetRow == pr + dir;

            case KNIGHT:
                int dc = Math.abs(pc - targetCol);
                int dr = Math.abs(pr - targetRow);
                return (dc == 1 && dr == 2) || (dc == 2 && dr == 1);

            case KING:
                return Math.abs(pc - targetCol) <= 1 &&
                       Math.abs(pr - targetRow) <= 1;

            case BISHOP:
                if (Math.abs(pc - targetCol) != Math.abs(pr - targetRow)) return false;
                return clearDiagonal(pc, pr, targetCol, targetRow);

            case ROOK:
                if (pc != targetCol && pr != targetRow) return false;
                return clearStraight(pc, pr, targetCol, targetRow);

            case QUEEN:
                if (pc == targetCol || pr == targetRow)
                    return clearStraight(pc, pr, targetCol, targetRow);
                if (Math.abs(pc - targetCol) == Math.abs(pr - targetRow))
                    return clearDiagonal(pc, pr, targetCol, targetRow);
                return false;
        }

        return false;
    }
    
    private boolean clearStraight(int c1, int r1, int c2, int r2) {
        int dc = Integer.signum(c2 - c1);
        int dr = Integer.signum(r2 - r1);

        int c = c1 + dc;
        int r = r1 + dr;

        while (c != c2 || r != r2) {
            if (board.getPiece(c, r) != null) return false;
            c += dc;
            r += dr;
        }
        return true;
    }

    private boolean clearDiagonal(int c1, int r1, int c2, int r2) {
        int dc = Integer.signum(c2 - c1);
        int dr = Integer.signum(r2 - r1);

        int c = c1 + dc;
        int r = r1 + dr;

        while (c != c2 || r != r2) {
            if (board.getPiece(c, r) != null) return false;
            c += dc;
            r += dr;
        }
        return true;
    }
    
    public List<Piece> getAttackersOfSquare(int col, int row, int byColor) {
        List<Piece> attackers = new ArrayList<>();

        for (Piece p : board.getAllPieces()) {
            if (p.getColor() != byColor) continue;
            if (attacksSquare(p, col, row)) {
                attackers.add(p);
            }
        }

        return attackers;
    }



}

