package chess.engine.pieces;

import main.BoardState;

public class Bishop  extends Piece{
	public Bishop(int col,int row, int color) {
		super(col, row, color);
		
		type = Type.BISHOP;
		
		 if (color == BoardState.WHITE){
		 	image = getImage("/Chess/images/Chess_blt60.png"); //white bishop image
		 }
		 else {
		 	image = getImage("/Chess/images/Chess_bdt60.png");//black bishop image
		 }
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			
			if (Math.abs(targetCol - preCol) == Math.abs(targetRow-preRow)) {
				if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		return false;
			
	}
	
}
