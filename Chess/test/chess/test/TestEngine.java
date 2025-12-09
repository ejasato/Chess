package chess.test;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.*;
import chess.engine.search.Engine;
import chess.engine.movegen.MoveGenerator;
import chess.engine.eval.Evaluator;
import chess.rules.MoveValidator;

public class TestEngine {

    public static void main(String[] args) {

        System.out.println("===== TEST SUITE START =====\n");

        testStartingPosition();
        testIllegalMove();
        testFoolsMate();
        testPromotion();
        testEngineSearch();

        System.out.println("\n===== TEST SUITE COMPLETE =====");
    }

    // -----------------------------------------------------------
    private static void testStartingPosition() {
        System.out.println("=== TEST 1: Starting Position ===");

        Board board = new Board();
        board.setupStartingPosition();
        board.printBoard();

        MoveGenerator gen = new MoveGenerator(board);
        Evaluator eval = new Evaluator();

        System.out.println("Legal moves for WHITE = " + gen.generateMoves(GamePanel.WHITE).size());
        System.out.println("Evaluation = " + eval.evaluate(board));
        System.out.println();
    }

    // -----------------------------------------------------------
    private static void testIllegalMove() {
        System.out.println("=== TEST 2: Illegal Move Rejection ===");

        Board board = new Board();
        board.setupStartingPosition();
        MoveGenerator gen = new MoveGenerator(board);

        // Try to move white king to E4 at start — illegal
        Move illegal = new Move(4, 7, 4, 4);

        boolean found = false;
        for (Move m : gen.generateMoves(GamePanel.WHITE)) {
            if (m.fromCol == illegal.fromCol &&
                m.fromRow == illegal.fromRow &&
                m.toCol == illegal.toCol &&
                m.toRow == illegal.toRow) {
                found = true;
            }
        }

        if (!found) {
            System.out.println("Correct: Illegal king move NOT generated.");
        } else {
            System.out.println("ERROR: Illegal king move WAS generated!");
        }

        System.out.println();
    }

    // -----------------------------------------------------------
    private static void testFoolsMate() {
        System.out.println("=== TEST 3: Fool's Mate ===");

        Board board = new Board();
        board.setupStartingPosition();

        MoveGenerator gen = new MoveGenerator(board);
        MoveValidator validator = new MoveValidator(board);

        // 1. f3
        board.makeMove(new Move(5, 6, 5, 5));
        // 1... e5
        board.makeMove(new Move(4, 1, 4, 3));
        // 2. g4
        board.makeMove(new Move(6, 6, 6, 4));
        // 2... Qh4#
        board.makeMove(new Move(3, 0, 7, 4));

        board.printBoard();

    }

    // -----------------------------------------------------------
    private static void testPromotion() {
        System.out.println("=== TEST 4: Promotion ===");

        Board board = new Board();
        GamePanel.simPieces.clear();

        Pawn p = new Pawn(0, 6, GamePanel.WHITE);
        board.setPiece(0, 6, p);
        GamePanel.simPieces.add(p);

        Move promote = new Move(0, 6, 0, 7);
        promote.isPromotion = true;
        promote.promotionType = Type.QUEEN;

        board.makeMove(promote);
        board.printBoard();

        System.out.println("Promotion complete.\n");
    }

    // -----------------------------------------------------------
    private static void testEngineSearch() {
        System.out.println("=== TEST 5: Engine Search (Depth 3) ===");

        Board board = new Board();
        board.setupStartingPosition();

        Engine engine = new Engine(board);
        Move best = engine.findBestMove(GamePanel.WHITE, 3);

        System.out.println("Engine chooses: " + best + "\n");
    }
}