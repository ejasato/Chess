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
	private boolean vsComputer = true; // default: vs AI
	private int aiColor = GamePanel.BLACK; // AI plays Black
	private boolean aiThinking = false;
	private int aiDepth = 3; // default difficulty (Medium-ish)
	private boolean pendingNewGame = false;


	// Selection & moves
	private Piece selectedPiece = null;
	private final List<Move> legalMoves = new ArrayList<>();
	private Move lastMove = null;
	private int gameId = 0;
	private long allowMouseTime = 0;


	// Snapshot used ONLY for drawing (prevents flicker)
	private static class PieceView {
		final int col, row, color;
		final Type type;

		PieceView(Piece p) {
			this.col = p.col;
			this.row = p.row;
			this.color = p.getColor();
			this.type = p.getType();
		}
	}

	private final List<PieceView> drawPieces = new ArrayList<>();

	// SVG images
	private final BufferedImage[][] pieceImages = new BufferedImage[2][6];

	// UI panels
	private MoveHistoryPanel historyPanel;
	private CapturedPanel capturedPanel;

	// Status label from GameFrame
	private final JLabel statusLabel;

	// Remember last winner (WHITE/BLACK or null for draw)
	private Integer lastWinner = null;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	public ChessPanel(JLabel statusLabel) {
		this.statusLabel = statusLabel;

		setPreferredSize(new Dimension(8 * TILE, 8 * TILE));
		setBackground(Color.BLACK);

		board = new Board();
		validator = new MoveValidator(board);
		engine = new Engine(board);

		loadAllSVGs();

		// REMOVE setupStartingPosition() here
		// REMOVE syncDrawPieces() here

		newGame(); // <-- THIS IS THE FIX

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleClick(e.getX(), e.getY());
			}
		});
		// Delay activation of mouse clicks to avoid phantom events
		allowMouseTime = System.currentTimeMillis() + 300;  // wait 300 ms

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
		if (depth < 1)
			depth = 1;
		this.aiDepth = depth;
	}

	public Integer getLastWinner() {
		return lastWinner;
	}

	// -------------------------------------------------------------------------
	// SVG loading
	// -------------------------------------------------------------------------
	private void loadAllSVGs() {
		String[] names = { "pawn", "rook", "knight", "bishop", "queen", "king" };

		for (int color = 0; color < 2; color++) {
			for (int t = 0; t < 6; t++) {
				String filename = (color == 0 ? "white_" : "black_") + names[t] + ".svg";
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
			String fullPath = "src/main/resources/" + filename;

			PNGTranscoder transcoder = new PNGTranscoder();
			transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 256f);
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 256f);

			TranscoderInput input = new TranscoderInput(new FileInputStream(fullPath));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(out);

			transcoder.transcode(input, output);

			return javax.imageio.ImageIO.read(new ByteArrayInputStream(out.toByteArray()));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// State helpers
	// -------------------------------------------------------------------------
	/** Take a snapshot of current simPieces for drawing only. */
	private void syncDrawPieces() {
		drawPieces.clear();
		for (Piece p : GamePanel.simPieces) {
			if (p != null) {
				drawPieces.add(new PieceView(p));
			}
		}
	}

	private void updateStatusLabel() {
		String side = (currentColor == GamePanel.WHITE) ? "White" : "Black";
		if (gameOver) {
			statusLabel.setText("Game over.");
		} else {
			statusLabel.setText(
					side + " to move" + (vsComputer ? " (vs Computer)" : " (1 vs 1)") + " | AI depth: " + aiDepth);
		}
	}

	private void showWinDialog(String message) {
		JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	// -------------------------------------------------------------------------
	// Input handling
	// -------------------------------------------------------------------------
	private void handleClick(int x, int y) {
		if (System.currentTimeMillis() < allowMouseTime) {
		    System.out.println("IGNORED PHANTOM CLICK");
		    return;
		}


		if (gameOver)
			return;
		if (aiThinking)
			return; // don't allow clicks during AI search

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
		if (selectedPiece == null)
			return;

		int fc = selectedPiece.col;
		int fr = selectedPiece.row;

		for (int c = 0; c < 8; c++) {
			for (int r = 0; r < 8; r++) {
				Move m = new Move(fc, fr, c, r);

				// piece move rules + king safety (validator)
				if (selectedPiece.canMove(c, r, board) && validator.isLegalMove(m, currentColor)) {
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
	    System.out.println(">>> applyMove CALLED | move=" + m +
	            " | currentColor=" + currentColor +
	            " | gameId=" + gameId);

	    if (gameOver)
	        return;

	    board.makeMove(m);
	    lastMove = m;

	    // UI capture display
	    if (m.captured != null && capturedPanel != null) {
	        BufferedImage img = pieceImages[m.captured.getColor()][m.captured.getType().ordinal()];
	        capturedPanel.addCapturedPiece(currentColor, img);
	    }

	    // KING CAPTURE
	    if (m.captured != null && m.captured.getType() == Type.KING) {
	        gameOver = true;
	        lastWinner = currentColor;

	        String winnerStr = (currentColor == GamePanel.WHITE) ? "White" : "Black";
	        statusLabel.setText("King captured – " + winnerStr + " wins!");

	        syncDrawPieces();
	        repaint();

	        showWinDialog("King captured – " + winnerStr + " wins!");

	        // ❌ No newGame() — let the board stay
	        return;
	    }

	    // Move history
	    if (historyPanel != null) {
	        historyPanel.addMove(m.toString());
	    }

	    selectedPiece = null;
	    legalMoves.clear();

	    syncDrawPieces();

	    // Checkmate / stalemate
	    evaluateGameStateAfterMove();

	    // Turn switch ONLY if game is not over
	    if (!gameOver) {
	        currentColor = (currentColor == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;
	    }

	    updateStatusLabel();
	    repaint();
	}


	/**
	 * Very simple check / mate / stalemate detection: - checks if side-to-move's
	 * king is in check - and if side-to-move has any legal moves.
	 */
	private void evaluateGameStateAfterMove() {

	    int sideJustMoved = currentColor;
	    int sideToMove = (currentColor == GamePanel.WHITE)
	            ? GamePanel.BLACK : GamePanel.WHITE;

	    boolean inCheck = validator.isKingInCheck(sideToMove);
	    boolean hasMoves = sideHasLegalMoves(sideToMove);

	    if (!hasMoves) {
	        gameOver = true;

	        if (inCheck) {
	            lastWinner = sideJustMoved;
	            String w = (sideJustMoved == GamePanel.WHITE) ? "White" : "Black";
	            statusLabel.setText("Checkmate! " + w + " wins.");
	            showWinDialog("Checkmate! " + w + " wins.");
	        } else {
	            lastWinner = null;
	            statusLabel.setText("Stalemate – draw.");
	            showWinDialog("Stalemate – draw.");
	        }

	        syncDrawPieces();
	        repaint();

	        // ❌ Do not auto-start a new game
	    }
	}




	private boolean sideHasLegalMoves(int color) {
		// Make a snapshot to avoid ConcurrentModification issues
		List<Piece> snapshot = new ArrayList<>(GamePanel.simPieces);

		for (Piece p : snapshot) {
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

	// -------------------------------------------------------------------------
	// AI move (background thread)
	// -------------------------------------------------------------------------
	private void makeAIMove() {

		System.out.println("\n=== makeAIMove CALLED ===");
		System.out.println("currentColor=" + currentColor + " | aiColor=" + aiColor + " | gameId=" + gameId
				+ " | aiThinking=" + aiThinking + " | gameOver=" + gameOver);

		if (gameOver) {
			System.out.println("AI CANCELLED: gameOver=true");
			return;
		}

		if (!vsComputer) {
			System.out.println("AI CANCELLED: vsComputer=false");
			return;
		}

		if (aiThinking) {
			System.out.println("AI CANCELLED: aiThinking=true");
			return;
		}

		aiThinking = true;
		final int thisGame = gameId;

		System.out.println("AI STARTED for gameId=" + thisGame + "\n");

		SwingWorker<Move, Void> worker = new SwingWorker<>() {
			@Override
			protected Move doInBackground() {
				return engine.findBestMove(currentColor, aiDepth);
			}

			@Override
			protected void done() {
				try {

					System.out.println("AI FINISHED for gameId=" + thisGame + " (current gameId=" + gameId + ")");

					// STOP old threads from applying moves after reset
					if (thisGame != gameId) {
						System.out.println("AI MOVE IGNORED: stale thread");
						return;
					}

					Move best = get();

					if (best != null && !gameOver) {
						System.out.println("AI APPLYING MOVE: " + best);
						applyMove(best);
					} else {
						System.out.println("AI: No valid move returned");
					}

				} catch (Exception ex) {
					System.out.println("AI ERROR: " + ex.getMessage());
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

		System.out.println("=== NEW GAME ===");

		// Stop any AI thinking
		aiThinking = false;

		// Invalidate all old AI threads
		gameId++;

		// ALWAYS start with white
		currentColor = GamePanel.WHITE;
		System.out.println("currentColor forced to WHITE");

		gameOver = false;

		selectedPiece = null;
		lastMove = null;
		legalMoves.clear();

		board.setupStartingPosition();
		syncDrawPieces();

		if (historyPanel != null)
			historyPanel.reset();
		if (capturedPanel != null)
			capturedPanel.reset();

		updateStatusLabel();
		repaint();

		// -----------------------------
		// IMPORTANT:
		// Only call AI if AI is WHITE.
		// -----------------------------
		if (vsComputer && aiColor == GamePanel.WHITE) {
			System.out.println("AI is WHITE — AI moves first.");
			makeAIMove();
		} else {
			System.out.println("AI is NOT white — human moves first.");
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
				g.setColor(light ? new Color(240, 217, 181) : new Color(181, 136, 99));
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

		// Draw pieces from snapshot (no flicker)
		for (PieceView pv : drawPieces) {
			BufferedImage img = pieceImages[pv.color][pv.type.ordinal()];
			if (img == null)
				continue;

			g.drawImage(img, pv.col * TILE, pv.row * TILE, TILE, TILE, null);
		}
	}
}
