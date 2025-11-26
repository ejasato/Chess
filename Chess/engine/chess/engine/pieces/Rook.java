package chess.engine.pieces;

public class Rook extends Piece{
	public Rook(int col, int row, int color) {
		super (col, row, color);
		
		type = Type.ROOK;
		
		if (color == GamePanel.WHITE) {
			image = getImage("/Chess/images/Chess_rdt60.png"); // white rook
		}
		else {
			image = getImage("/Chess/images/Chess_rlt60.png");
		}
			
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			
			if(targetCol == preCol || targetRow == preRow) {
				if (isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
}
