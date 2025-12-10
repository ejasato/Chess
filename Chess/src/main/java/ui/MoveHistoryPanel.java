package ui;

import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;
import chess.model.pieces.GamePanel;
import chess.rules.MoveValidator;

import javax.swing.*;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final JTextArea textArea;
    private int moveNumber = 1;

    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 8 * 64));

        JLabel title = new JLabel("Moves");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(textArea);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------------------
    // PUBLIC: Called from ChessPanel.applyMove(...)
    // -------------------------------------------------------------------------
    public void addMove(Move move, Piece piece, Board board, MoveValidator validator) {
        String notation = buildNotation(move, piece, board, validator);
        textArea.append(moveNumber + ". " + notation + "\n");
        moveNumber++;
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    // -------------------------------------------------------------------------
    // Build PGN notation for a move
    // -------------------------------------------------------------------------
    private String buildNotation(Move move, Piece piece, Board board, MoveValidator validator) {

        // 1. CASTLING -------------------------------
        if (piece.getType() == Type.KING) {
            if (move.fromCol == 4 && move.toCol == 6) return "O-O";     // kingside
            if (move.fromCol == 4 && move.toCol == 2) return "O-O-O";   // queenside
        }

        StringBuilder sb = new StringBuilder();

        // 2. PIECE LETTER ---------------------------
        if (piece.getType() != Type.PAWN) {
            sb.append(pieceLetter(piece));
        }

        boolean isCapture = (move.captured != null);

        // 3. Pawn capture includes file letter ------
        if (piece.getType() == Type.PAWN && isCapture) {
            sb.append((char)('a' + move.fromCol));
        }

        // 4. Capture marker -------------------------
        if (isCapture) sb.append("x");

        // 5. Destination square ---------------------
        sb.append((char)('a' + move.toCol));
        sb.append(8 - move.toRow);

        // 6. Promotion ------------------------------
        if (move.isPromotion && move.promotionType != null) {
            sb.append("=");
            sb.append(pieceLetter(move.promotionType));
        }

        // 7. Check / Checkmate ----------------------
        int enemyColor = (piece.getColor() == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);

        boolean enemyInCheck = validator.isKingInCheck(enemyColor);
        boolean enemyHasMoves = sideHasLegalMoves(board, validator, enemyColor);

        if (!enemyHasMoves && enemyInCheck) {
            sb.append("#");   // checkmate
        } else if (enemyInCheck) {
            sb.append("+");   // check
        }

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String pieceLetter(Piece p) {
        return pieceLetter(p.getType());
    }

    private String pieceLetter(Type t) {
        switch (t) {
            case KING: return "K";
            case QUEEN: return "Q";
            case ROOK: return "R";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case PAWN: return "";
        }
        return "";
    }

    // Used for check/checkmate detection
    private boolean sideHasLegalMoves(Board board, MoveValidator validator, int color) {
        for (Piece p : board.getAllPieces()) {
            if (p.getColor() != color)
                continue;

            int fc = p.col;
            int fr = p.row;

            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    Move m = new Move(fc, fr, c, r);
                    if (p.canMove(c, r, board) && validator.isLegalMove(m, color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getMoveCount() {
        return moveNumber - 1;
    }

    
    public void reset() {
        textArea.setText("");
        moveNumber = 1;
    }
}

