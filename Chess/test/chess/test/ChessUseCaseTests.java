package chess.test;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.*;
import chess.engine.movegen.MoveGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChessUseCaseTests {

    /** Clear the board and simPieces **/
    private void clear(Board board) {
        GamePanel.simPieces.clear();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board.setPiece(c, r, null);
    }

    // -----------------------------------------------------
    // USE CASE 1: CHECKMATE (Fool's Mate)
    // -----------------------------------------------------
    @Test
    public void testCheckmateFoolsMate_byMoveList() {
        Board board = new Board();
        board.setupStartingPosition();

        MoveGenerator gen = new MoveGenerator(board);

        // Fool's mate:
        board.makeMove(new Move(5, 6, 5, 5)); // f3
        board.makeMove(new Move(4, 1, 4, 3)); // e5
        board.makeMove(new Move(6, 6, 6, 4)); // g4
        board.makeMove(new Move(3, 0, 7, 4)); // Qh4

        // At checkmate, WHITE should have 0 legal moves
        assertEquals(0, gen.generateMoves(GamePanel.WHITE).size(),
                "White should have no legal moves (checkmate by move count).");
    }

    // -----------------------------------------------------
    // USE CASE 2: PAWN PROMOTION
    // -----------------------------------------------------
    @Test
    public void testPawnPromotion_basic() {
        Board board = new Board();
        clear(board);

        // White pawn on 7th rank
        Pawn pawn = new Pawn(0, 6, GamePanel.WHITE);
        board.setPiece(0, 6, pawn);
        GamePanel.simPieces.add(pawn);

        Move promote = new Move(0, 6, 0, 7);
        promote.isPromotion = true;
        promote.promotionType = Type.QUEEN;

        board.makeMove(promote);

        Piece result = board.getPiece(0, 7);
        assertTrue(result instanceof Queen,
                "Pawn should promote to Queen.");
    }

    @Test
    public void testPawnPromotion_defaultToQueenOnInvalid() {
        Board board = new Board();
        clear(board);

        Pawn pawn = new Pawn(1, 6, GamePanel.WHITE);
        board.setPiece(1, 6, pawn);
        GamePanel.simPieces.add(pawn);

        Move promote = new Move(1, 6, 1, 7);
        promote.isPromotion = true;
        promote.promotionType = null; // invalid

        board.makeMove(promote);

        assertTrue(board.getPiece(1, 7) instanceof Queen,
                "Invalid promotionType should default to Queen.");
    }

    // -----------------------------------------------------
    // USE CASE 3: CAPTURE
    // -----------------------------------------------------
    @Test
    public void testCapture_byDestinationState() {
        Board board = new Board();
        clear(board);

        Rook rook = new Rook(0, 7, GamePanel.WHITE);
        Pawn enemyPawn = new Pawn(0, 5, GamePanel.BLACK);

        board.setPiece(0, 7, rook);
        board.setPiece(0, 5, enemyPawn);
        GamePanel.simPieces.add(rook);
        GamePanel.simPieces.add(enemyPawn);

        // Check capture move appears in move list
        MoveGenerator gen = new MoveGenerator(board);
        boolean found = gen.generateMoves(GamePanel.WHITE)
                .stream()
                .anyMatch(m -> m.fromCol == 0 && m.fromRow == 7 && m.toCol == 0 && m.toRow == 5);
        assertTrue(found, "Capture move must appear in the generated move list.");

        // Execute capture
        board.makeMove(new Move(0, 7, 0, 5));

        assertTrue(board.getPiece(0, 5) instanceof Rook,
                "Rook must move into captured pawn's square.");
    }

    // -----------------------------------------------------
    // USE CASE 4: EN PASSANT
    // -----------------------------------------------------
    @Test
    public void testEnPassant_basic() {
        Board board = new Board();
        clear(board);

        Pawn whitePawn = new Pawn(4, 4, GamePanel.WHITE);
        Pawn blackPawn = new Pawn(5, 6, GamePanel.BLACK);

        board.setPiece(4, 4, whitePawn);
        board.setPiece(5, 6, blackPawn);
        GamePanel.simPieces.add(whitePawn);
        GamePanel.simPieces.add(blackPawn);

        // Black pawn double-step
        Move doubleStep = new Move(5, 6, 5, 4);
        board.makeMove(doubleStep);

        MoveGenerator gen = new MoveGenerator(board);

        // Check en passant move appears in pseudo-legal move list
        boolean found = gen.generateMoves(GamePanel.WHITE)
                .stream()
                .anyMatch(m -> m.fromCol == 4 && m.fromRow == 4 &&
                               m.toCol == 5 && m.toRow == 5);
        assertTrue(found, "En passant should appear in generated moves.");

        // Execute the en passant
        board.makeMove(new Move(4, 4, 5, 5));

        assertTrue(board.getPiece(5, 5) instanceof Pawn,
                "White pawn must land behind black pawn (en passant).");

        // The captured pawn must be removed
        assertNull(board.getPiece(5, 4),
                "Black pawn should be removed in en passant.");
    }

    // -----------------------------------------------------
    // USE CASE 5: CASTLING
    // -----------------------------------------------------
    @Test
    public void testKingsideCastle_basic() {
        Board board = new Board();
        clear(board);

        King king = new King(4, 7, GamePanel.WHITE);
        Rook rook = new Rook(7, 7, GamePanel.WHITE);

        board.setPiece(4, 7, king);
        board.setPiece(7, 7, rook);
        GamePanel.simPieces.add(king);
        GamePanel.simPieces.add(rook);

        MoveGenerator gen = new MoveGenerator(board);

        // Castle move should appear in move generator output
        boolean found = gen.generateMoves(GamePanel.WHITE)
                .stream()
                .anyMatch(m -> m.fromCol == 4 && m.fromRow == 7 &&
                               m.toCol == 6 && m.toRow == 7);
        assertTrue(found, "Kingside castle move should appear in move list.");

        // Execute castle
        board.makeMove(new Move(4, 7, 6, 7));

        assertTrue(board.getPiece(6, 7) instanceof King,
                "King must move to G1/G8 (col 6).");

        assertTrue(board.getPiece(5, 7) instanceof Rook,
                "Rook must move to F1/F8 (col 5).");
    }
}
