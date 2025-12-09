package chess.model.pieces;

import chess.model.board.Board;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Piece {

    protected Type type;
    public int col, row;
    protected int color;
    protected BufferedImage image;

    // used for pawn double-step / en-passant support
    protected boolean twoStepped = false;

    public Piece(int col, int row, int color) {
        this.col = col;
        this.row = row;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    // GUI support only (safe)
    public BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            return null;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    // coordinate helpers
    public int getX() {
        return col * Board.SQUARE_SIZE;
    }

    public int getY() {
        return row * Board.SQUARE_SIZE;
    }

    // ------------------------------------------------------------------
    //  Two-step helpers (for pawns / en-passant)
    // ------------------------------------------------------------------
    public boolean hasTwoStepped() {
        return twoStepped;
    }

    public void setTwoStepped(boolean twoStepped) {
        this.twoStepped = twoStepped;
    }

    // ------------------------------------------------------------------
    // REQUIRED ABSTRACT MOVEMENT LOGIC
    // ------------------------------------------------------------------
    public abstract boolean canMove(int targetCol, int targetRow, Board board);

    // Helpers: board boundaries
    protected boolean inside(int c, int r) {
        return c >= 0 && c < 8 && r >= 0 && r < 8;
    }

    // Check if square is empty or capturable
    protected boolean canCaptureOrMove(int tc, int tr, Board board) {
        Piece p = board.getPiece(tc, tr);
        return p == null || p.color != this.color;
    }

    // Path clearance for rook/queen
    protected boolean clearStraight(int tc, int tr, Board board) {
        if (tc == col) {
            int step = Integer.signum(tr - row);
            for (int r = row + step; r != tr; r += step) {
                if (board.getPiece(tc, r) != null) return false;
            }
            return true;
        }
        if (tr == row) {
            int step = Integer.signum(tc - col);
            for (int c = col + step; c != tc; c += step) {
                if (board.getPiece(c, row) != null) return false;
            }
            return true;
        }
        return false;
    }

    // Path clearance for bishop/queen
    protected boolean clearDiagonal(int tc, int tr, Board board) {
        int dc = tc - col;
        int dr = tr - row;
        if (Math.abs(dc) != Math.abs(dr)) return false;

        int stepC = Integer.signum(dc);
        int stepR = Integer.signum(dr);

        int c = col + stepC;
        int r = row + stepR;

        while (c != tc && r != tr) {
            if (board.getPiece(c, r) != null) return false;
            c += stepC;
            r += stepR;
        }
        return true;
    }
    
    public abstract Piece copy();

}
