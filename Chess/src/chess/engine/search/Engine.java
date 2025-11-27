package chess.engine.search;

import chess.engine.eval.Evaluator;
import chess.engine.movegen.MoveGenerator;
import chess.model.board.Board;
import chess.model.move.Move;

import java.util.List;

/**
 * Simple engine: depth-limited alpha-beta with static evaluation.
 * Uses MoveGenerator + Board.makeMove/undoMove.
 */
public class Engine {

    private final Board board;
    private final MoveGenerator gen;
    private final Evaluator eval;
    private volatile boolean stop = false;
    
    //engine constructor
    public Engine(Board board) {
        this.board = board;
        this.gen = new MoveGenerator(board);
        this.eval = new Evaluator();
    }
    
    public void stopSearch() { stop = true; }
    
    //finds the best move and gives a 0 second answer
    public Move findBestMove(int color, int maxDepth) {
        return findBestMove(color, maxDepth, 0L);
    }
    //finds the best move and can be better moves depending on time
    public Move findBestMove(int color, int maxDepth, long timeMs) {
        stop = false;
        long endTime = timeMs > 0 ? System.currentTimeMillis() + timeMs : Long.MAX_VALUE;
        Move best = null;

        for (int depth = 1; depth <= maxDepth && !stop; depth++) {
            int alpha = -1_000_000;
            int beta = 1_000_000;
            Move currentBest = null;

            List<Move> moves = gen.generateMoves(color);
            for (Move m : moves) {
                if (System.currentTimeMillis() > endTime) { stop = true; break; }

                board.makeMove(m);
                int score = -alphaBeta(1 - color, depth - 1, -beta, -alpha, endTime);
                board.undoMove(m);

                if (score > alpha) {
                    alpha = score;
                    currentBest = m;
                }
            }

            if (currentBest != null) best = currentBest;
            if (System.currentTimeMillis() > endTime) break;
        }

        return best;
    }
    
    //finds the best move by simulating all moves and branches off
    //and picks the best branch with the best score
    private int alphaBeta(int color, int depth, int alpha, int beta, long endTime) {
        if (stop || System.currentTimeMillis() > endTime) return 0;

        if (depth == 0) {
            return eval.evaluate(board);
        }

        int best = -1_000_000;
        List<Move> moves = gen.generateMoves(color);
        if (moves.isEmpty()) {
            // no moves -> evaluate position (no full mate detection yet)
            return eval.evaluate(board);
        }

        for (Move m : moves) {
            if (System.currentTimeMillis() > endTime) { stop = true; break; }

            board.makeMove(m);
            int score = -alphaBeta(1 - color, depth - 1, -beta, -alpha, endTime);
            board.undoMove(m);

            if (score > best) best = score;
            if (score > alpha) alpha = score;
            if (alpha >= beta) break; // beta cutoff
        }

        return best;
    }
}