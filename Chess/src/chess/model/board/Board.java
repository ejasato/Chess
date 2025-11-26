package chess.model.board;

import chess.model.move.Move;
import chess.model.pieces.*;
import java.util.Iterator;
import chess.model.pieces.Type;

/**
 * Represents an 8x8 chess board with Piece references.
 * Coordinates: col 0..7 (files a..h), row 0..7 (ranks 8..1).
 */
public class Board {

    // sizes used by Piece for drawing
    public static final int SQUARE_SIZE = 64;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    private final Piece[][] squares = new Piece[8][8];

    // ---------- BASIC ACCESSORS ----------

    public boolean isInside(int col, int row) {
        return col >= 0 && col < 8 && row >= 0 && row < 8;
    }

    public Piece getPiece(int col, int row) {
        if (!isInside(col, row)) return null;
        return squares[row][col];
    }

    public void setPiece(int col, int row, Piece piece) {
        if (!isInside(col, row)) return;
        squares[row][col] = piece;
        if (piece != null) {
            piece.col = col;
            piece.row = row;
        }
    }

    // ---------- MOVE EXECUTION / UNDO ----------

    public void makeMove(Move move) {
        Piece moving = getPiece(move.fromCol, move.fromRow);
        Piece captured = getPiece(move.toCol, move.toRow);

        move.moved = moving;
        move.captured = captured;

        if (moving != null) {
            moving.preCol = moving.col;
            moving.preRow = moving.row;
        }

        // remove captured from simPieces list
        if (captured != null) {
            GamePanel.simPieces.remove(captured);
        }

        // move the piece
        setPiece(move.toCol, move.toRow, moving);
        setPiece(move.fromCol, move.fromRow, null);

        // very simple promotion (optional)
        if (move.isPromotion && moving != null && move.promotionType != null) {
            promotePiece(move.toCol, move.toRow, moving.getColor(), move.promotionType);
        }
    }

    public void undoMove(Move move) {
        Piece moving = move.moved;
        Piece captured = move.captured;

        // move piece back
        setPiece(move.fromCol, move.fromRow, moving);
        setPiece(move.toCol, move.toRow, captured);

        if (moving != null) {
            moving.col = move.fromCol;
            moving.row = move.fromRow;
        }

        // re-add captured to simPieces
        if (captured != null && !GamePanel.simPieces.contains(captured)) {
            GamePanel.simPieces.add(captured);
        }
    }

    // ---------- PROMOTION HELPER ----------

    private void promotePiece(int col, int row, int color, Type type) {
        Piece newPiece;

        switch (type) {
            case QUEEN:
                newPiece = new Queen(col, row, color);
                break;
            case ROOK:
                newPiece = new Rook(col, row, color);
                break;
            case BISHOP:
                newPiece = new Bishop(col, row, color);
                break;
            case KNIGHT:
                newPiece = new Knight(col, row, color);
                break;
            default:
                return;
        }

        // place onto board array
        setPiece(col, row, newPiece);

        // replace in simPieces list
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            Piece p = GamePanel.simPieces.get(i);
            if (p.col == col && p.row == row) {
                GamePanel.simPieces.set(i, newPiece);
                break;
            }
        }
    }
    private Piece movePieceAt(int col, int row) {
        return getPiece(col, row);
    }

    // ---------- STARTING POSITION ----------

    public void setupStartingPosition() {
        GamePanel.simPieces.clear();

        // Clear board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        int WHITE = GamePanel.WHITE;
        int BLACK = GamePanel.BLACK;

        // White pieces
        setAndAdd(new Rook(0, 7, WHITE));
        setAndAdd(new Knight(1, 7, WHITE));
        setAndAdd(new Bishop(2, 7, WHITE));
        setAndAdd(new Queen(3, 7, WHITE));
        setAndAdd(new King(4, 7, WHITE));
        setAndAdd(new Bishop(5, 7, WHITE));
        setAndAdd(new Knight(6, 7, WHITE));
        setAndAdd(new Rook(7, 7, WHITE));
        for (int c = 0; c < 8; c++) {
            setAndAdd(new Pawn(c, 6, WHITE));
        }

        // Black pieces
        setAndAdd(new Rook(0, 0, BLACK));
        setAndAdd(new Knight(1, 0, BLACK));
        setAndAdd(new Bishop(2, 0, BLACK));
        setAndAdd(new Queen(3, 0, BLACK));
        setAndAdd(new King(4, 0, BLACK));
        setAndAdd(new Bishop(5, 0, BLACK));
        setAndAdd(new Knight(6, 0, BLACK));
        setAndAdd(new Rook(7, 0, BLACK));
        for (int c = 0; c < 8; c++) {
            setAndAdd(new Pawn(c, 1, BLACK));
        }
    }

    private void setAndAdd(Piece p) {
        setPiece(p.col, p.row, p);
        GamePanel.simPieces.add(p);
    }

    // ---------- DEBUG PRINT ----------

    public void printBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = squares[r][c];
                if (p == null) {
                    System.out.print(". ");
                } else {
                    char ch = p.getType().toString().charAt(0);
                    if (p.getColor() != GamePanel.WHITE)
                        ch = Character.toLowerCase(ch);
                    System.out.print(ch + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}