package ui;

import chess.engine.search.Engine;
import chess.model.board.Board;
import chess.model.move.Move;
import chess.model.pieces.GamePanel;
import chess.model.pieces.Piece;
import chess.model.pieces.Type;
import chess.rules.MoveValidator;
import chess.user.Player;
import chess.user.PlayerDatabase;
import chess.user.LoginDialog;


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

	// Logged in player
	private Player currentPlayer;

	// Game state
	private int currentColor = GamePanel.WHITE;
	private boolean gameOver = false;
	private boolean vsComputer = true;
	private int aiColor = GamePanel.BLACK;
	private boolean aiThinking = false;
	private int aiDepth = 3;
	private int gameId = 0;
	private long allowMouseTime = 0;

	// Selection
	private Piece selectedPiece = null;
	private final List<Move> legalMoves = new ArrayList<>();
	private Move lastMove = null;

	// Drawing snapshot
	private static class PieceView {
		final int col, row, color;
		final Type type;
		PieceView(Piece p) {
			col = p.col; row = p.row; color = p.getColor(); type = p.getType();
		}
	}
	private final List<PieceView> drawPieces = new ArrayList<>();

	// Images
	private final BufferedImage[][] pieceImages = new BufferedImage[2][6];

	// UI
	private MoveHistoryPanel historyPanel;
	private CapturedPanel capturedPanel;
	private final JLabel statusLabel;

	private Integer lastWinner = null;

	// Constructor
	public ChessPanel(JLabel statusLabel) {
		this.statusLabel = statusLabel;

		setPreferredSize(new Dimension(8 * TILE, 8 * TILE));
		setBackground(Color.BLACK);

		board = new Board();
		validator = new MoveValidator(board);
		engine = new Engine(board);

		loadAllSVGs();
		newGame();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleClick(e.getX(), e.getY());
			}
		});

		allowMouseTime = System.currentTimeMillis() + 300;
	}

	// Hooks from GameFrame
	public void setHistoryPanel(MoveHistoryPanel p) {
		historyPanel = p;
	}

	public void setCapturedPanel(CapturedPanel p) {
		capturedPanel = p;
	}

	public void setVsComputer(boolean vs) {
		vsComputer = vs;
	}

	public void setAIDepth(int depth) {
		if (depth < 1) depth = 1;
		aiDepth = depth;
	}

	public void setCurrentPlayer(Player p) {
		currentPlayer = p;
	}

	// SVG loading
	private void loadAllSVGs() {
		String[] names = { "pawn", "rook", "knight", "bishop", "queen", "king" };

		for (int color = 0; color < 2; color++) {
			for (int t = 0; t < 6; t++) {
				String filename = (color == 0 ? "white_" : "black_") + names[t] + ".svg";
				pieceImages[color][t] = loadSVG(filename);
			}
		}
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

	private void syncDrawPieces() {
		drawPieces.clear();
		for (Piece p : GamePanel.simPieces)
			drawPieces.add(new PieceView(p));
	}

	private void updateStatusLabel() {
		if (gameOver) {
			statusLabel.setText("Game over.");
			return;
		}
		String side = (currentColor == GamePanel.WHITE ? "White" : "Black");
		statusLabel.setText(side + " to move | AI depth " + aiDepth);
	}

	// Click handling
	private void handleClick(int x, int y) {
		if (System.currentTimeMillis() < allowMouseTime) return;
		if (gameOver) return;
		if (aiThinking) return;
		if (vsComputer && currentColor == aiColor) return;

		int col = x / TILE;
		int row = y / TILE;

		if (!board.isInside(col, row)) return;

		Piece clicked = board.getPiece(col, row);

		if (selectedPiece == null) {
			if (clicked != null && clicked.getColor() == currentColor) {
				selectedPiece = clicked;
				computeLegalMoves();
			}
		} else {
			if (clicked != null && clicked.getColor() == currentColor) {
				selectedPiece = clicked;
				computeLegalMoves();
			} else {
				Move m = findMove(col, row);
				if (m != null) {
					applyMove(m);
					if (vsComputer && !gameOver && currentColor == aiColor)
						makeAIMove();
				} else {
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
				if (selectedPiece.canMove(c, r, board) && validator.isLegalMove(m, currentColor))
					legalMoves.add(m);
			}
		}
	}

	private Move findMove(int col, int row) {
		for (Move m : legalMoves)
			if (m.toCol == col && m.toRow == row)
				return m;
		return null;
	}

	private void applyMove(Move m) {
		if (gameOver) return;

		// Promotion
		if (selectedPiece != null &&
			selectedPiece.getType() == Type.PAWN &&
			(m.toRow == 0 || m.toRow == 7)) {

			m.isPromotion = true;
			if (!vsComputer || currentColor != aiColor)
				m.promotionType = askPromotionPiece();
			else
				m.promotionType = Type.QUEEN;
		}

		board.makeMove(m);
		lastMove = m;

		if (m.captured != null && capturedPanel != null) {
			BufferedImage img = pieceImages[m.captured.getColor()][m.captured.getType().ordinal()];
			capturedPanel.addCapturedPiece(currentColor, img);
		}

		if (historyPanel != null)
			historyPanel.addMove(m, m.moved, board, validator);

		selectedPiece = null;
		legalMoves.clear();
		syncDrawPieces();

		evaluateGameStateAfterMove();

		if (!gameOver)
			currentColor = (currentColor == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);

		updateStatusLabel();
		repaint();
	}

	private void evaluateGameStateAfterMove() {

		int sideJustMoved = currentColor;
		int sideToMove = (currentColor == GamePanel.WHITE ? GamePanel.BLACK : GamePanel.WHITE);

		boolean inCheck = validator.isKingInCheck(sideToMove);
		boolean hasMoves = sideHasLegalMoves(sideToMove);

		if (!hasMoves) {
			gameOver = true;

			if (inCheck) {
				lastWinner = sideJustMoved;
				String w = (sideJustMoved == GamePanel.WHITE) ? "White" : "Black";
				showWinDialog("Checkmate! " + w + " wins.");

				// Record stats
				if (currentPlayer != null) {
					if (sideJustMoved == GamePanel.WHITE)
						currentPlayer.whiteWins++;
					else
						currentPlayer.blackWins++;

					PlayerDatabase.update(currentPlayer);
				}

			} else {
				lastWinner = null;
				showWinDialog("Stalemate – Draw.");
			}

			syncDrawPieces();
			return;
		}
	}
	
	public Player getCurrentPlayer() {
	    return currentPlayer;
	}


	private boolean sideHasLegalMoves(int color) {
		List<Piece> snapshot = new ArrayList<>(GamePanel.simPieces);

		for (Piece p : snapshot) {
			if (p.getColor() != color) continue;

			for (int c = 0; c < 8; c++) {
				for (int r = 0; r < 8; r++) {
					Move m = new Move(p.col, p.row, c, r);
					if (p.canMove(c, r, board) && validator.isLegalMove(m, color))
						return true;
				}
			}
		}
		return false;
	}

	private void showWinDialog(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}

	// AI move
	private void makeAIMove() {
		if (gameOver) return;
		if (!vsComputer) return;
		if (aiThinking) return;

		aiThinking = true;
		final int thisGame = gameId;

		SwingWorker<Move, Void> worker = new SwingWorker<>() {
			@Override
			protected Move doInBackground() {
				return engine.findBestMove(currentColor, aiDepth);
			}

			@Override
			protected void done() {
				try {
					if (thisGame != gameId) return;

					Move best = get();
					if (best != null && !gameOver)
						applyMove(best);

				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					aiThinking = false;
				}
			}
		};

		worker.execute();
	}

	// New game
	public void newGame() {
		aiThinking = false;
		gameId++;

		currentColor = GamePanel.WHITE;
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

		if (vsComputer && aiColor == GamePanel.WHITE)
			makeAIMove();
	}

	// Drawing
	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;

		// Board
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				boolean light = ((c + r) % 2 == 0);
				g.setColor(light ? new Color(240, 217, 181) : new Color(181, 136, 99));
				g.fillRect(c * TILE, r * TILE, TILE, TILE);
			}
		}

		// Last move highlight
		if (lastMove != null) {
			g.setColor(new Color(255, 255, 0, 100));
			g.fillRect(lastMove.fromCol * TILE, lastMove.fromRow * TILE, TILE, TILE);
			g.fillRect(lastMove.toCol * TILE, lastMove.toRow * TILE, TILE, TILE);
		}

		// Legal move dots
		if (selectedPiece != null) {
			g.setColor(new Color(0, 0, 0, 140));
			for (Move m : legalMoves) {
				int x = m.toCol * TILE + TILE / 3;
				int y = m.toRow * TILE + TILE / 3;
				g.fillOval(x, y, TILE / 3, TILE / 3);
			}
		}

		// Pieces
		for (PieceView pv : drawPieces) {
			BufferedImage img = pieceImages[pv.color][pv.type.ordinal()];
			g.drawImage(img, pv.col * TILE, pv.row * TILE, TILE, TILE, null);
		}
	}

	// Promotion dialog
	private Type askPromotionPiece() {
		String[] options = { "Queen", "Rook", "Bishop", "Knight" };
		int choice = JOptionPane.showOptionDialog(
				this, "Choose promotion piece:", "Pawn Promotion",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]
		);

		return switch (choice) {
			case 1 -> Type.ROOK;
			case 2 -> Type.BISHOP;
			case 3 -> Type.KNIGHT;
			default -> Type.QUEEN;
		};
	}

}

