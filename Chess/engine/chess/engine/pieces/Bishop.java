package chess.engine.pieces;

public class Bishop  extends Piece{
	public Bishop(int col,int row, int color) {
		super(col, row, color);
		
		type = Type.BISHOP;
		
		// if (color == Game.WHITE){
		// 	image = getImage(); //white bishop image
		// }
		// else {
		// 	image = getImage();//black bishop image
		// }
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
