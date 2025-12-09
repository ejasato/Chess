package ui;

import chess.engine.search.Engine;
import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.GamePanel;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;
import chess.rules.MoveValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class ChessPanel extends JPanel {

    public static final int TILE = 64;

    // Game model
    private final Board board;
    private final MoveValidator validator;
    private final Engine engine;

    // Turn + mode
    private int currentColor = GamePanel.WHITE;
    private boolean gameOver = false;
    private boolean vsComputer = true;      // default: vs AI
    private int aiColor = GamePanel.BLACK;  // AI plays Black
    private boolean aiThinking = false;
    private int aiDepth = 3;                // default difficulty (Medium-ish)

    // Selection & moves
    private Piece selectedPiece = null;
    private final List<Move> legalMoves = new ArrayList<>();
    private Move lastMove = null;

    // To avoid flicker / ConcurrentModification:
    // we draw from drawPieces, which is a snapshot copy of simPieces.
    private final List<Piece> drawPieces = new ArrayList<>();

    // SVG images
    private final BufferedImage[][] pieceImages = new BufferedImage[2][6];

    // UI panels
    private MoveHistoryPanel historyPanel;
    private CapturedPanel capturedPanel;

    // Status label from GameFrame
    private final JLabel statusLabel;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public ChessPanel(JLabel statusLabel) {
        this.statusLabel = statusLabel;

        setPreferredSize(new Dimension(8 * TILE, 8 * TILE));
        setBackground(Color.BLACK);

        board = new Board();
        board.setupStartingPosition();
        syncDrawPieces();

        validator = new MoveValidator(board);
        engine = new Engine(board);

        loadAllSVGs();
        updateStatusLabel();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    // -------------------------------------------------------------------------
    // Hooks from GameFrame
    // -------------------------------------------------------------------------
    public void setHistoryPanel(MoveHistoryPanel p) {
        this.historyPanel = p;
    }

    public void setCapturedPanel(CapturedPanel p) {
        this.capturedPanel = p;
    }

    public void setVsComputer(boolean vsComputer) {
        this.vsComputer = vsComputer;
    }

    public void setAIDepth(int depth) {
        if (depth < 1) depth = 1;
        this.aiDepth = depth;
    }

    // -------------------------------------------------------------------------
    // SVG loading
    // -------------------------------------------------------------------------
    private void loadAllSVGs() {
        String[] names = {"pawn", "rook", "knight", "bishop", "queen", "king"};

        for (int color = 0; color < 2; color++) {
            for (int t = 0; t < 6; t++) {
                String filename =
                        (color == 0 ? "white_" : "black_") + names[t] + ".svg";
                pieceImages[color][t] = loadSVG(filename);
                if (pieceImages[color][t] == null) {
                    System.out.println("FAILED to load SVG: " + filename);
                }
            }
        }
        System.out.println("Loaded all SVG chess pieces.");
    }

    private BufferedImage loadSVG(String filename) {
        try {
            String fullPath = "src/res/" + filename;

            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 256f);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 256f);

            TranscoderInput input = new TranscoderInput(new FileInputStream(fullPath));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(out);

            transcoder.transcode(input, output);

            return javax.imageio.ImageIO.read(
                    new ByteArrayInputStream(out.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // State helpers
    // -------------------------------------------------------------------------
    private void syncDrawPieces() {
        drawPieces.clear();
        drawPieces.addAll(GamePanel.simPieces);
    }

    private void updateStatusLabel() {
        String side = (currentColor == GamePanel.WHITE) ? "White" : "Black";
        if (gameOver) {
            statusLabel.setText("Game over. " + side + " to move (but game ended).");
        } else {
            statusLabel.setText(side + " to move" +
                    (vsComputer ? " (vs Computer)" : " (1 vs 1)") +
                    " | AI depth: " + aiDepth);
        }
    }

    // -------------------------------------------------------------------------
    // Input handling
    // -------------------------------------------------------------------------
    private void handleClick(int x, int y) {
        if (gameOver) return;
        if (aiThinking) return; // don't allow clicks during AI search

        // If vs computer, only allow human side clicks
        if (vsComputer && currentColor == aiColor) {
            return;
        }

        int col = x / TILE;
        int row = y / TILE;

        if (!board.isInside(col, row))
            return;

        Piece clicked = board.getPiece(col, row);

        if (selectedPiece == null) {
            // select a piece of the current side
            if (clicked != null && clicked.getColor() == currentColor) {
                selectedPiece = clicked;
                computeLegalMoves();
            }
        } else {
            // clicking own piece -> reselect
            if (clicked != null && clicked.getColor() == currentColor) {
                selectedPiece = clicked;
                computeLegalMoves();
            } else {
                // attempt move
                Move m = findMove(col, row);
                if (m != null) {
                    applyMove(m);

                    // If now it's AI's turn and we are vs computer, trigger AI move
                    if (vsComputer && !gameOver && currentColor == aiColor) {
                        makeAIMove();
                    }
                } else {
                    // invalid target, deselect
                    selectedPiece = null;
                    legalMoves.clear();
                }
            }
        }

        repaint();
    }

    private void computeLegalMoves() {
        legalMoves.clear();
        if (selectedPiece == null) return;

        int fc = selectedPiece.col;
        int fr = selectedPiece.row;

        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                Move m = new Move(fc, fr, c, r);

                // piece move rules + king safety
                if (selectedPiece.canMove(c, r, board) &&
                    validator.isLegalMove(m, currentColor)) {
                    legalMoves.add(m);
                }
            }
        }
    }

    private Move findMove(int col, int row) {
        for (Move m : legalMoves) {
            if (m.toCol == col && m.toRow == row)
                return m;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Applying a move (used by both human and AI)
    // -------------------------------------------------------------------------
    private void applyMove(Move m) {
        if (gameOver) return;

        board.makeMove(m);
        lastMove = m;

        // Handle capture for UI panel
        if (m.captured != null && capturedPanel != null) {
            BufferedImage img = pieceImages[m.captured.getColor()]
                    [m.captured.getType().ordinal()];
            capturedPanel.addCapturedPiece(currentColor, img);
        }

        // Move history
        if (historyPanel != null) {
            historyPanel.addMove(m.toString());
        }

        // Clear selection
        selectedPiece = null;
        legalMoves.clear();

        // Refresh drawPieces snapshot AFTER board/simPieces have been updated
        syncDrawPieces();

        // Check for checkmate / stalemate using MoveValidator-only version:
        evaluateGameState();

        // Switch turn if game not over
        if (!gameOver) {
            currentColor = (currentColor == GamePanel.WHITE)
                    ? GamePanel.BLACK : GamePanel.WHITE;
        }

        updateStatusLabel();
        repaint();
    }

    /**
     * Very simple check / mate / stalemate detection:
     * - checks if current side's king is in check
     * - and if current side has any legal moves.
     *
     * NOTE: This uses the same kind of logic as your MoveValidator,
     * not the separate CheckMateDetector class, to keep things stable.
     */
    private void evaluateGameState() {
        // who just moved? that's opposite of currentColor BEFORE we flipped
        int sideJustMoved = (currentColor == GamePanel.WHITE)
                ? GamePanel.BLACK : GamePanel.WHITE;

        // Is the opponent (sideJustMoved) delivering check to the new side?
        boolean kingInCheck = validator.isKingInCheck(currentColor);

        boolean hasMoves = sideHasLegalMoves(currentColor);

        if (!hasMoves) {
            gameOver = true;
            if (kingInCheck) {
                statusLabel.setText(
                        ((currentColor == GamePanel.WHITE) ? "White" : "Black")
                                + " has no moves and is in check – Checkmate!");
            } else {
                statusLabel.setText("Stalemate – no legal moves.");
            }
        }
    }

    private boolean sideHasLegalMoves(int color) {
        // Make a snapshot to avoid ConcurrentModification issues
        List<Piece> snapshot = new ArrayList<>(GamePanel.simPieces);

        for (Piece p : snapshot) {
            if (p.getColor() != color) continue;

            int fc = p.col;
            int fr = p.row;

            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    Move m = new Move(fc, fr, c, r);
                    if (p.canMove(c, r, board) &&
                        validator.isLegalMove(m, color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // AI move (background thread)
    // -------------------------------------------------------------------------
    private void makeAIMove() {
        if (gameOver) return;
        if (!vsComputer) return;
        if (aiThinking) return;

        aiThinking = true;

        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override
            protected Move doInBackground() {
                // Engine uses current board and depth
                return engine.findBestMove(currentColor, aiDepth);
            }

            @Override
            protected void done() {
                try {
                    Move best = get();
                    if (best != null && !gameOver) {
                        applyMove(best);
                    }
                } catch (Exception ignored) {
                } finally {
                    aiThinking = false;
                }
            }
        };
        worker.execute();
    }

    // -------------------------------------------------------------------------
    // New game
    // -------------------------------------------------------------------------
    public void newGame() {
        gameOver = false;
        currentColor = GamePanel.WHITE;
        selectedPiece = null;
        lastMove = null;
        legalMoves.clear();

        board.setupStartingPosition();
        syncDrawPieces();

        if (historyPanel != null) historyPanel.reset();
        if (capturedPanel != null) capturedPanel.reset();

        updateStatusLabel();
        repaint();

        // If vs computer and AI plays White, let it move first
        if (vsComputer && currentColor == aiColor) {
            makeAIMove();
        }
    }

    // -------------------------------------------------------------------------
    // Drawing
    // -------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        // Board squares
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boolean light = (c + r) % 2 == 0;
                g.setColor(light ? new Color(240, 217, 181)
                                 : new Color(181, 136, 99));
                g.fillRect(c * TILE, r * TILE, TILE, TILE);
            }
        }

        // Last move highlight
        if (lastMove != null) {
            g.setColor(new Color(255, 255, 0, 90));
            g.fillRect(lastMove.fromCol * TILE, lastMove.fromRow * TILE, TILE, TILE);
            g.fillRect(lastMove.toCol * TILE, lastMove.toRow * TILE, TILE, TILE);
        }

        // Selected square and legal moves
        if (selectedPiece != null) {
            g.setColor(new Color(0, 255, 0, 80));
            g.fillRect(selectedPiece.col * TILE, selectedPiece.row * TILE, TILE, TILE);

            g.setColor(new Color(0, 0, 0, 140));
            for (Move m : legalMoves) {
                int x = m.toCol * TILE + TILE / 3;
                int y = m.toRow * TILE + TILE / 3;
                g.fillOval(x, y, TILE / 3, TILE / 3);
            }
        }

        // Draw pieces from snapshot (no concurrent modification, no flicker)
        for (Piece p : drawPieces) {
            if (p == null) continue;

            BufferedImage img = pieceImages[p.getColor()][p.getType().ordinal()];
            if (img == null) continue;

            g.drawImage(img,
                    p.col * TILE,
                    p.row * TILE,
                    TILE, TILE,
                    null);
        }
    }
}
