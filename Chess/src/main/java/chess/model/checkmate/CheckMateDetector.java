package chess.model.checkmate;

import chess.model.board.Board;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

import java.util.ArrayList;
import java.util.List;

public class CheckMateDetector {

    private final MoveSimulator simulator;
    private final Board board;

    public CheckMateDetector(MoveSimulator sim) {
        this.simulator = sim;
        this.board = sim.getBoard();
    }

    public boolean isKingInCheck(int color) {
        return simulator.isKingInCheck(color);
    }

    public boolean isCheckMate(int color) {

        Piece king = findKing(color);
        if (king == null) return false;

        if (!isKingInCheck(color)) return false;

        if (kingCanMove(king)) return false;

        List<Piece> attackers = findAttackers(king, oppositeColor(color));

        if (attackers.isEmpty()) return false;

        if (attackers.size() >= 2) {
            return true;
        }

        Piece attacker = attackers.get(0);

        if (canCaptureAttacker(attacker, color)) return false;

        if (canBlockAttack(king, attacker, color)) return false;

        return true;
    }

    public boolean isStalemate(int color) {

        if (isKingInCheck(color)) return false;

        for (Piece p : board.getAllPieces()) {
            if (p.getColor() != color) continue;

            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    if (simulator.isLegalMove(p, c, r)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private int oppositeColor(int color) {
        return (color == 0 ? 1 : 0);
    }

    private Piece findKing(int color) {
        for (Piece p : board.getAllPieces()) {
            if (p.getColor() == color && p.getType() == Type.KING)
                return p;
        }
        return null;
    }

    private List<Piece> findAttackers(Piece king, int enemyColor) {
        return simulator.getAttackersOfSquare(king.col, king.row, enemyColor);
    }


    private boolean canCaptureAttacker(Piece attacker, int color) {
        for (Piece ally : board.getAllPieces()) {
            if (ally.getColor() != color) continue;

            if (simulator.isLegalMove(ally, attacker.col, attacker.row)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBlockAttack(Piece king, Piece attacker, int color) {

        if (attacker.getType() == Type.KNIGHT) return false;
        if (attacker.getType() == Type.PAWN)   return false;

        int dc = Integer.signum(king.col - attacker.col);
        int dr = Integer.signum(king.row - attacker.row);

        int c = attacker.col + dc;
        int r = attacker.row + dr;

        while (c != king.col || r != king.row) {

            for (Piece ally : board.getAllPieces()) {
                if (ally.getColor() != color) continue;

                if (simulator.isLegalMove(ally, c, r)) {
                    return true;
                }
            }

            c += dc;
            r += dr;
        }

        return false;
    }

    private boolean kingCanMove(Piece king) {
        int[][] dirs = {
                {-1,-1},{0,-1},{1,-1},
                {-1, 0},       {1, 0},
                {-1, 1},{0, 1},{1, 1}
        };

        for (int[] d : dirs) {
            if (simulator.isValidKingMove(king, d[0], d[1])) {
                return true;
            }
        }

        return false;
    }
}


