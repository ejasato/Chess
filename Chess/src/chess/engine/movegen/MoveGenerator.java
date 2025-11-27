package chess.engine.movegen;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    private final Board board;
    //constructor
    public MoveGenerator(Board board) {
        this.board = board;
    }

    //generates move and checks if legal(whether king is in check after the move)
    public List<Move> generateMoves(int color) {
        List<Move> legal = new ArrayList<>();
        List<Move> pseudo = generatePseudoMoves(color);

        for (Move m : pseudo) {
            // Simulate
            board.makeMove(m);

            // Check king safety
            if (!isKingInCheck(color)) {
                legal.add(m);
            }

            // Undo
            board.undoMove(m);
        }

        return legal;
    }

    //simulates move instead of implementing, to use in testing for check
    private List<Move> generatePseudoMoves(int color) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Piece p = board.getPiece(c, r);
                if (p == null || p.getColor() != color)
                    continue;

                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (p.canMove(tc, tr)) {
                            moves.add(new Move(c, r, tc, tr));
                        }
                    }
                }
            }
        }
        return moves;
    }

    //Checks if king is in check
    public boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        int enemy = (color == 0 ? 1 : 0);

        // All enemy pseudo moves
        List<Move> enemyMoves = generatePseudoMoves(enemy);

        for (Move m : enemyMoves) {
            if (m.toCol == king.col && m.toRow == king.row) {
                return true;
            }
        }
        return false;
    }
    //finds a specific colors king on the board
    private Piece findKing(int color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(c, r);
                if (p != null && p.getColor() == color && p.getType() == Type.KING) {
                    return p;
                }
            }
        }
        return null;
    }

    //checks for king in check and ends the game
    public boolean isCheckmate(int color) {
        if (!isKingInCheck(color)) return false;
        return generateMoves(color).isEmpty();
    }

    //if a certain amount of moves have been done in a row then its stalemate
    public boolean isStalemate(int color) {
        if (isKingInCheck(color)) return false;
        return generateMoves(color).isEmpty();
    }
}