package chess.engine.pieces;

import GUI.Board;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;


public abstract class Piece {

	protected Type type;
	public int x, y;
	public int col, row, preCol, preRow;
	protected int color;
	protected Piece toCapture;
	public BufferedImage image;
	protected boolean moved,  twoStepped;
	public Piece hittingP;
	
	// constructor
	public Piece(int col, int row, int color) {
		this.col = col;
		this.row = row;
		this.color = color;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}
	
	//Image display for a piece
	 public BufferedImage getImage(String imagePath) {
	 	BufferedImage image = null;
		try {
			image = ImageIO.read(Piece.class.getResourceAsStream(imagePath + ".png"));
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public boolean getTwoStepped() {
		return this.twoStepped;
	}
	
	public void setTwoStepped(boolean value) {
		this.twoStepped = value;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public Type getType() {
		return this.type;
	}
	public int getX(int col) {
		return  col*Board.SQUARE_SIZE;// return current piece position in the x
	}
	public int getY(int row) {
		return  row*Board.SQUARE_SIZE;// return current piece position in the y
	}
	
	public int getCol(int x) {
		return (x + Board.HALF_SQUARE_SIZE)/ Board.SQUARE_SIZE;
	}
	
	public int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE)/ Board.SQUARE_SIZE;

	}
	
	public Piece getHittingP(int targetCol, int targetRow) {
		for (Piece piece : GamePanel.simPieces) {
			if(piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public int getIndex() {
		for (int index = 0; index < GamePanel.simPieces.size(); index++) {
			if(GamePanel.simPieces.get(index) == this) {
				return index;
			}
		}
		return 0;
	}
	
	public void draw(Graphics graph) {
		graph.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	
	public void updatePosition() {
		// check en passant
		if (type == Type.PAWN) {
			if (Math.abs(row - preRow) == 2) {
				twoStepped = true;
			}
		}
		
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		moved = true;
		
	}
	
	public abstract boolean canMove(int targetCol, int targetRow) ;
	
	public boolean isWithinBoard(int targetCol, int targetRow) {
		if (targetCol >= 0 && targetRow <= 7 &&
				targetRow >= 0 && targetCol <= 7) {
			return true;
		}
		return false;
	}
	
	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}
	
	public boolean isValidSquare(int targetCol, int targetRow) {
		hittingP = getHittingP(targetCol, targetRow);
		if (hittingP == null) {
			return true;
		}
		else {
			if (hittingP.color != this.color){
				return true;
				
			}
			else {
				hittingP = null;
			}
		}
		return false;
	}
	
	public boolean isSameSquare(int targetCol, int targetRow) {
		if (targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
		
	}
	
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
		
		for (int col = preCol -1; col > targetCol; col--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == col && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		for (int col = preCol -1; col< targetCol; col++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == col && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		for (int row = preRow - 1; row > targetRow; row--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == row) {
					hittingP = piece;
					return true;
				}
			}
		}

		for (int row = preRow - 1; row < targetRow; row++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == row) {
					hittingP = piece;
					return true;
				}
			}
		}
		return false;
		
	}
	
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
		if (targetRow < preRow) {
			for (int col = preCol - 1;col > targetCol; col-- ) {
				int difference = Math.abs(col - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == col && piece.row == preRow - difference) {
						hittingP = piece;
						return true;
					}
				}
						
			}
			
			for (int col = preCol + 1;col < targetCol; col ++ ) {
				int difference = Math.abs(col - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == col && piece.row == preRow - difference) {
						hittingP = piece;
						return true;
					}
				}
						
			}
		}
		
		
		if (targetRow > preRow) {
			for (int col = preCol - 1;col > targetCol; col-- ) {
				int difference = Math.abs(col - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == col && piece.row == preRow + difference) {
						hittingP = piece;
						return true;
					}
				}
						
			}
			
			for (int col = preCol + 1;col < targetCol; col ++ ) {
				int difference = Math.abs(col - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == col && piece.row == preRow + difference) {
						hittingP = piece;
						return true;
					}
				}
						
			}
		}
		return false;
	}
	
}
