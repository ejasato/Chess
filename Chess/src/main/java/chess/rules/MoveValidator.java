//package chess.rules;
//
//import chess.model.board.Board;
//import chess.model.move.Move;
//import chess.model.pieces.GamePanel;
//import chess.model.pieces.Piece;
//import chess.model.pieces.Type;
//
//public class MoveValidator {
//
//    private final Board board;
//
//    public MoveValidator(Board board) {
//        this.board = board;
//    }
//
//    /** -----------------------------------------------------------
//     *  Checks if king of given color is in check
//     * ----------------------------------------------------------- */
//    public boolean isKingInCheck(int color) {
//
//        Piece king = findKing(color);
//        if (king == null) return false;
//
//        int enemy = (color == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);
//
//        // Loop through enemy pieces
//        for (Piece p : GamePanel.simPieces) {
//
//            if (p.getColor() != enemy) continue;
//
//            // Use board-aware canMove
//            if (p.canMove(king.col, king.row, board)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /** -----------------------------------------------------------
//     *  Find the king of a color
//     * ----------------------------------------------------------- */
//    private Piece findKing(int color) {
//        for (Piece p : GamePanel.simPieces) {
//            if (p.getColor() == color && p.getType() == Type.KING) {
//                return p;
//            }
//        }
//        return null;
//    }
//
//    /** -----------------------------------------------------------
//     *  Checks if a move is legal:
//     *  - piece must exist & be correct color
//     *  - must obey piece movement rules
//     *  - must not leave own king in check
//     * ----------------------------------------------------------- */
//    public boolean isLegalMove(Move move, int color) {
//
//        Piece moving = board.getPiece(move.fromCol, move.fromRow);
//        if (moving == null) {
//            return false;
//        }
//
//        // wrong color
//        if (moving.getColor() != color) {
//            return false;
//        }
//
//        // obey piece move rules first
//        if (!moving.canMove(move.toCol, move.toRow, board)) {
//            return false;
//        }
//
//        // simulate
//        board.makeMove(move);
//        boolean inCheck = isKingInCheck(color);
//        board.undoMove(move);
//
//        return !inCheck;
//    }
//}
//

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

    // ---------------- KING IN CHECK ----------------

    public boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        int enemy = (color == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);

        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() != enemy) continue;

            if (p.canMove(king.col, king.row, board)) {
                return true;
            }
        }
        return false;
    }

    private Piece findKing(int color) {
        for (Piece p : GamePanel.simPieces) {
            if (p.getColor() == color && p.getType() == Type.KING) {
                return p;
            }
        }
        return null;
    }

    // ---------------- LEGAL MOVE CHECK ----------------

    public boolean isLegalMove(Move move, int color) {

        Piece moving = board.getPiece(move.fromCol, move.fromRow);
        if (moving == null) return false;

        if (moving.getColor() != color) return false;

        boolean isEnPassant = false;
        boolean isCastle = false;

        // ---- PAWN (including EN PASSANT) ----
        if (moving.getType() == Type.PAWN) {

            // Normal pawn moves/captures
            if (moving.canMove(move.toCol, move.toRow, board)) {
                // OK, nothing special
            } else {
                // Check en passant pattern
                int dir = (color == GamePanel.WHITE ? -1 : 1);

                boolean candidateEP =
                        move.toRow == move.fromRow + dir &&
                        Math.abs(move.toCol - move.fromCol) == 1 &&
                        board.getPiece(move.toCol, move.toRow) == null;

                if (!candidateEP) {
                    return false;
                }

                Move last = board.lastMove;
                if (last == null || last.moved == null) {
                    return false;
                }

                Piece lastPiece = last.moved;

                if (!(lastPiece.getType() == Type.PAWN &&
                      lastPiece.getColor() != color &&
                      Math.abs(last.toRow - last.fromRow) == 2 &&
                      last.toRow == move.fromRow &&
                      last.toCol == move.toCol)) {

                    return false;
                }

                // It is a valid en passant
                isEnPassant = true;
            }

        // ---- KING (including CASTLING) ----
        } else if (moving.getType() == Type.KING) {
            int dc = move.toCol - move.fromCol;
            int dr = move.toRow - move.fromRow;

            boolean castleAttempt = (dr == 0 && Math.abs(dc) == 2);

            if (castleAttempt) {
                boolean kingSide = (dc > 0);
                if (!canCastle(color, kingSide)) {
                    return false;
                }
                isCastle = true;
            } else {
                if (!moving.canMove(move.toCol, move.toRow, board)) {
                    return false;
                }
            }

        // ---- ALL OTHER PIECES ----
        } else {
            if (!moving.canMove(move.toCol, move.toRow, board)) {
                return false;
            }
        }

        // ---- SIMULATE MOVE TO CHECK SELF-CHECK ----
        Move prevLast = board.lastMove; // preserve
        boolean oldEP = move.isEnPassant;
        boolean oldCastle = move.isCastle;

        move.isEnPassant = isEnPassant;
        move.isCastle = isCastle;

        board.makeMove(move);
        boolean inCheck = isKingInCheck(color);
        board.undoMove(move);

        board.lastMove = prevLast;      // restore
        move.isEnPassant = oldEP;
        move.isCastle = oldCastle;

        return !inCheck;
    }

    // ---------------- CASTLING RULES ----------------

    private boolean canCastle(int color, boolean kingSide) {
        Piece king = findKing(color);
        if (king == null) return false;

        int row = king.row;
        int kingCol = king.col;

        int rookCol = kingSide ? 7 : 0;
        Piece rook = board.getPiece(rookCol, row);
        if (rook == null || rook.getType() != Type.ROOK || rook.getColor() != color) {
            return false;
        }

        // Squares between king and rook must be empty
        int step = (rookCol > kingCol) ? 1 : -1;
        for (int c = kingCol + step; c != rookCol; c += step) {
            if (board.getPiece(c, row) != null) return false;
        }

        // King cannot be in check
        if (isKingInCheck(color)) return false;

        // Squares king passes through must not be attacked
        int midCol = kingSide ? kingCol + 1 : kingCol - 1;

        Move prevLast = board.lastMove;

        // test moving king one square (without moving rook)
        Move temp = new Move(kingCol, row, midCol, row);
        board.makeMove(temp);
        boolean checkMid = isKingInCheck(color);
        board.undoMove(temp);
        board.lastMove = prevLast;

        if (checkMid) return false;

        // Final square safety is checked in isLegalMove via full simulation
        return true;
    }
}

