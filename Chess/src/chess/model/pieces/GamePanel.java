package chess.model.pieces;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal stub so existing Piece / Pawn / King code compiles.
 * Later you can replace this with a real UI GamePanel if you want.
 */
public class GamePanel {

    // colors
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    // list of all pieces in the current position
    public static final List<Piece> simPieces = new ArrayList<>();

    // used by castling code in King
    public static Piece castlingP;
}