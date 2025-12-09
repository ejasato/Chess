package chess.model.checkmate;

import chess.model.board.Board;
import chess.model.pieces.GamePanel;
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

    // ---------------------------------------------------------
    // 1. CHECK IF KING OF A COLOR IS IN CHECK
    // ---------------------------------------------------------
    public boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        int enemyColor = oppositeColor(color);
        return isSquareAttacked(king.col, king.row, enemyColor);
    }

    // ---------------------------------------------------------
    // 2. CHECKMATE LOGIC
    // ---------------------------------------------------------
    public boolean isCheckMate(int color) {

        Piece king = findKing(color);
        if (king == null) return false;

        // If king is not in check, no checkmate
        if (!isKingInCheck(color)) return false;

        // If king has any legal moves, no checkmate
        if (kingCanMove(king)) return false;

        // Find all attackers
        List<Piece> attackers = findAttackers(king, oppositeColor(color));

        // Double check: only king moves can resolve → but we've already tried that
        if (attackers.size() >= 2) {
            return true;
        }

        Piece attacker = attackers.get(0);

        // Can we capture the attacker safely?
        if (canCaptureAttacker(attacker, color)) return false;

        // Can we block the attack?
        if (canBlockAttack(king, attacker, color)) return false;

        return true;
    }

    // ---------------------------------------------------------
    // 3. STALEMATE
    // ---------------------------------------------------------
    public boolean isStalemate(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        // Stalemate requires king NOT in check
        if (isKingInCheck(color)) return false;

        // If any legal move exists for any piece of this color → no stalemate
        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() != color) continue;

            for (int col = 0; col < 8; col++) {
                for (int row = 0; row < 8; row++) {
                    if (simulator.isLegalMove(p, col, row)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // ---------------------------------------------------------
    // HELPER METHODS
    // ---------------------------------------------------------

    private int oppositeColor(int color) {
        return (color == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;
    }

    private Piece findKing(int color) {
        for (Piece p : GamePanel.simPieces) {
            if (p.getType() == Type.KING && p.getColor() == color) {
                return p;
            }
        }
        return null;
    }

    private boolean isSquareAttacked(int col, int row, int byColor) {
        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() != byColor) continue;
            if (p.canMove(col, row, board)) {
                return true;
            }
        }
        return false;
    }

    private List<Piece> findAttackers(Piece king, int enemyColor) {
        List<Piece> attackers = new ArrayList<>();

        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() == enemyColor &&
                p.canMove(king.col, king.row, board)) {
                attackers.add(p);
            }
        }
        return attackers;
    }

    private boolean canCaptureAttacker(Piece attacker, int color) {

        for (Piece ally : GamePanel.simPieces) {
            if (ally.getColor() != color) continue;

            // King capturing attacker is allowed if legal (simulator checks safety)
            if (simulator.isLegalMove(ally, attacker.col, attacker.row)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBlockAttack(Piece king, Piece attacker, int color) {

        // Knights and Pawns cannot be blocked; only captured
        if (attacker.getType() == Type.KNIGHT) return false;
        if (attacker.getType() == Type.PAWN)   return false;

        int dc = Integer.signum(king.col - attacker.col);
        int dr = Integer.signum(king.row - attacker.row);

        int col = attacker.col + dc;
        int row = attacker.row + dr;

        // Walk from attacker to king (excluding both)
        while (col != king.col || row != king.row) {
            for (Piece ally : GamePanel.simPieces) {
                if (ally.getColor() != color) continue;

                if (simulator.isLegalMove(ally, col, row)) {
                    return true;
                }
            }
            col += dc;
            row += dr;
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

