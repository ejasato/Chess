package chess.engine.search;

import chess.engine.eval.Evaluator;
import chess.engine.movegen.MoveGenerator;
import chess.model.board.Board;
import chess.model.move.Move;

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
        return findBestMove(color, maxDepth, 0L);
    }

    /**
     * Iterative deepening alpha-beta.
     * We only return the final best move – we never "commit" intermediate moves
     * to the UI; those intermediate makes/undos are purely internal.
     */
    public Move findBestMove(int color, int maxDepth, long timeMs) {
        stop = false;
        if (maxDepth < 1) maxDepth = 1;

        long endTime = timeMs > 0 
                ? System.currentTimeMillis() + timeMs 
                : Long.MAX_VALUE;

        Move best = null;

        // Iterative deepening
        for (int depth = 1; depth <= maxDepth && !stop; depth++) {
            int alpha = Integer.MIN_VALUE / 2;
            int beta  = Integer.MAX_VALUE / 2;
            Move currentBest = null;

            List<Move> moves = gen.generateMoves(color);
            if (moves.isEmpty()) {
                // no legal moves → let caller handle check / stalemate
                break;
            }

            for (Move m : moves) {
                if (System.currentTimeMillis() > endTime) { 
                    stop = true; 
                    break; 
                }

                board.makeMove(m);
                int score = -alphaBeta(1 - color, depth - 1, -beta, -alpha, endTime);
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

            if (System.currentTimeMillis() > endTime) break;
        }

        return best;
    }

    /**
     * Core alpha-beta search.
     */
    private int alphaBeta(int color, int depth, int alpha, int beta, long endTime) {
        if (stop || System.currentTimeMillis() > endTime) {
            stop = true;
            return 0;
        }

        if (depth == 0) {
            return eval.evaluate(board);
        }

        List<Move> moves = gen.generateMoves(color);
        if (moves.isEmpty()) {
            // No moves – just static eval for now; checkmate / stalemate
            // should be handled in the UI using check detectors.
            return eval.evaluate(board);
        }

        int best = Integer.MIN_VALUE / 2;

        for (Move m : moves) {
            if (System.currentTimeMillis() > endTime) {
                stop = true;
                break;
            }

            board.makeMove(m);
            int score = -alphaBeta(1 - color, depth - 1, -beta, -alpha, endTime);
            board.undoMove(m);

            if (score > best) best = score;
            if (score > alpha) alpha = score;

            if (alpha >= beta) { // beta cutoff
                break;
            }
        }

        return best;
    }
}


