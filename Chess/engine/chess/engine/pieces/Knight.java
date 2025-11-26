package chess.engine.pieces;

public class Knight extends Piece{
	
	public Knight(int col, int row, int color) {
		super (col, row, color);
		
		type = Type.KNIGHT;
		
		 if (color == GamePanel.WHITE) {
		 	image = getImage("/Chess/images/Chess_nlt60.png"); // white knight
		 }
		 else {
		 	image = getImage("/Chess/images/Chess_ndt60.png"); // black knight
		 }
		
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow)) {
			
			if (Math.abs(targetCol -preCol) * Math.abs(targetRow - preRow) == 2) {
				if (isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
}
