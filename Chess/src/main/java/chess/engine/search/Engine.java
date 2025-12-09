package chess.engine.search;

import chess.engine.eval.Evaluator;
import chess.engine.movegen.MoveGenerator;
import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.GamePanel;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private final Board board;
    private final MoveGenerator gen;
    private final Evaluator eval;
    private volatile boolean stop = false;

    // engine constructor uses the same Board position for root,
    // but we only ever leave it in the original state after search
    public Engine(Board board) {
        this.board = board;
        this.gen   = new MoveGenerator(board);
        this.eval  = new Evaluator();
    }

    public void stopSearch() {
        stop = true;
    }

    // Convenience: no time limit, only depth
    public Move findBestMove(int color, int maxDepth) {
        return findBestMove(color, maxDepth, 2000);
    }

    /**
     * Iterative deepening alpha-beta.
     * We only return the final best move – we never "commit" intermediate moves
     * to the UI; those makes/undos are purely internal.
     */
    public Move findBestMove(int color, int maxDepth, long timeMs) {
        stop = false;
        if (maxDepth < 1) maxDepth = 1;

        long endTime = (timeMs > 0)
                ? System.currentTimeMillis() + timeMs
                : Long.MAX_VALUE;

        Move best = null;

        // Iterative deepening
        for (int depth = 1; depth <= maxDepth && !stop; depth++) {
            int alpha = Integer.MIN_VALUE / 2;
            int beta  = Integer.MAX_VALUE / 2;
            Move currentBest = null;

            // **ONLY legal moves** for this color
            List<Move> moves = generateLegalMoves(color);
            if (moves.isEmpty()) {
                // no legal moves → UI decides if mate or stalemate
                break;
            }

            for (Move m : moves) {
                if (System.currentTimeMillis() > endTime) {
                    stop = true;
                    break;
                }

                board.makeMove(m);
                int score = -alphaBeta(oppositeColor(color), depth - 1,
                                       -beta, -alpha, endTime);
                board.undoMove(m);

                if (score > alpha) {
                    alpha = score;
                    currentBest = m;
                }

                if (stop) break;
            }

            if (currentBest != null) {
                best = currentBest;
            }

            if (System.currentTimeMillis() > endTime) {
                break;
            }
        }

        return best;
    }

    /** Core alpha-beta search. */
    private int alphaBeta(int color, int depth, int alpha, int beta, long endTime) {
        if (stop || System.currentTimeMillis() > endTime) {
            stop = true;
            return 0;
        }

        if (depth == 0) {
            return eval.evaluate(board);
        }

        // again, only legal moves
        List<Move> moves = generateLegalMoves(color);
        if (moves.isEmpty()) {
            // no legal moves – checkmate/stalemate handled at UI level
            return eval.evaluate(board);
        }

        int best = Integer.MIN_VALUE / 2;

        for (Move m : moves) {
            if (System.currentTimeMillis() > endTime) {
                stop = true;
                break;
            }

            board.makeMove(m);
            int score = -alphaBeta(oppositeColor(color), depth - 1,
                                   -beta, -alpha, endTime);
            board.undoMove(m);

            if (score > best) best = score;
            if (score > alpha) alpha = score;

            if (alpha >= beta) {   // beta cutoff
                break;
            }
        }

        return best;
    }

    // -----------------------------------------------------------------
    // Helpers: legal move generation & king-in-check logic for engine
    // -----------------------------------------------------------------
    private List<Move> generateLegalMoves(int color) {
        List<Move> all = gen.generateMoves(color);
        List<Move> legal = new ArrayList<>(all.size());

        for (Move m : all) {
            if (isMoveLegal(m, color)) {
                legal.add(m);
            }
        }
        return legal;
    }

    private boolean isMoveLegal(Move move, int color) {
        board.makeMove(move);
        boolean inCheck = isKingInCheck(color);
        board.undoMove(move);
        return !inCheck;
    }

    private boolean isKingInCheck(int color) {
        Piece king = findKing(color);
        if (king == null) return false;

        int enemy = oppositeColor(color);

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
            if (p.getType() == Type.KING && p.getColor() == color) {
                return p;
            }
        }
        return null;
    }

    private int oppositeColor(int color) {
        return (color == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;
    }
}
