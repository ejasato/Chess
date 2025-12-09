package chess.model.pieces;

import java.util.ArrayList;
import java.util.List;

public class GamePanel {

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final List<Piece> simPieces = new ArrayList<>();
    public static Piece castlingP;

    private int currentColor = WHITE;

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int color) {
        currentColor = color;
    }

    public void flipCurrentColor() {
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
    }

    // ... rest of your methods (pieces, getKing, getsimPieces, etc.)
}

