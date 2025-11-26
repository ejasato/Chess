package main;

import chess.engine.pieces.*;
import GUI.Mouse;
import GUI.Board;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	final int FPS = 60;
	Thread gameThread;
	
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	private final BoardState state = new BoardState();
	
	private final PieceManager pieceManager;
	private final MoveSimulator simulator;
	private final CheckMateDetector checker;
	private final PromotionHandler promotionHandler;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		pieceManager = new PieceManager(state.pieces, state.simPieces);
		simulator = new MoveSimulator(state, pieceManager);
		checker = new CheckMateDetector(state, pieceManager, simulator);
		promotionHandler = new PromotionHandler(state, pieceManager);
		
		pieceManager.setPieces();
		pieceManager.copyToSim();
	}
	
	public BoardState getBS() {
		return this.state;
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		double drawInterval = (double) 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	public void update() {
		if (state.promotion) {
			
			if(!mouse.pressed) {
				
			}
		}
		
		else if(!state.gameOver && !state.stalemate) {
			
			if (mouse.pressed) {
				if(state.activeP == null) {
					
					for (Piece piece : state.simPieces) {
						if (piece.getColor() == state.getCurrentColor() && 
								piece.col == mouse.x / Board.SQUARE_SIZE &&
								piece.row == mouse.y / Board.SQUARE_SIZE) {
							state.activeP = piece;
						}
						
					}
				}
				else {
					simulator.simulate(mouse.x, mouse.y);
				}
			}
			
			if(!mouse.pressed) {
				if (state.activeP != null) {
					if (state.validSquare) {
						
						pieceManager.copyFromSim();
						state.activeP.updatePosition();
						if(state.castlingP != null) {
							state.castlingP.updatePosition();
						}
						
						if (checker.isKingInCheck() && checker.isCheckMate()) {
							state.gameOver = true;
						}
						else if (checker.isStalemate() && !checker.isKingInCheck()) {
							state.stalemate = true;
						}
						else {
							if (promotionHandler.canPromote()) {
								state.promotion = true;
							}
							else {
								state.flipCurrentColor();
								pieceManager.resetTwoStepFor(state.getCurrentColor());
								state.activeP = null;
							}
						}
					}
					else {
						pieceManager.copyToSim();
						state.activeP.resetPosition();
						state.activeP = null;
					}
				}
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		board.draw(g2);
		
		for (Piece p : state.simPieces) {
			p.draw(g2);
		}
		
		
		if (state.activeP != null) {
			if (state.canMove) {
				if (simulator.isIllegal(state.activeP) || simulator.opponentCanCaptureKing()) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.7f));
					g2.fillRect(state.activeP.col*Board.SQUARE_SIZE, state.activeP.row * Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
				}
				else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.7f));
					g2.fillRect(state.activeP.col*Board.SQUARE_SIZE, state.activeP.row * Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					
					
				}
			}
			state.activeP.draw(g2);
		}
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		g2.setColor(Color.white);
		
		if (state.promotion) {
			g2.drawString("Promote to:", 630, 150);
			RenderHelper.drawPromotionChoices(g2, promotionHandler.getPromoPieces());
		}
		else {
			if(state.getCurrentColor() == BoardState.WHITE) {
				g2.drawString("White's turn", 630, 490);
				if (state.checkingP != null && state.checkingP.getColor() == BoardState.WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 630, 50);
					g2.drawString("is in check!", 630, 80);
				}
			}
			else {
				g2.drawString("Black's turn", 630, 490);
				if (state.checkingP != null && state.checkingP.getColor() == BoardState.BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 630, 50);
					g2.drawString("is in check!", 630, 80);
				}
			}
		}
		
		if (state.gameOver) {
			String s = (state.getCurrentColor() == BoardState.WHITE) ? "White Wins" : "Black Wins";
		
			g2.setColor(new Color(0,0,0,128));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setFont(new Font("Arial", Font.BOLD, 90));
			g2.setColor(Color.green);
			g2.drawString(s, 200, 320);
		}
		if (state.stalemate) {
			g2.setColor(new Color(0,0,0,128));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setFont(new Font("Arial", Font.BOLD, 90));
			g2.setColor(Color.lightGray);
			g2.drawString("Stalemate", 200, 320);
		}
		
		
		
	}
	public void onMouseReleased() {
		if (state.promotion) {
			boolean promoted = promotionHandler.tryPromote(mouse.x, mouse.y);
			
			if (promoted) {
				
			}
					
		}
	}
	
}
