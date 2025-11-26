package chess.engine.pieces;

public class Queen extends Piece{
	public Queen(int col, int row, int color) {
		super(col,row, color);
		
		type = Type.QUEEN;
		
		if (color == GamePanel.WHITE) {
			image = getImage("/Chess/images/Chess_qlt60.png"); // white queen
		}
		else {
			image = getImage("/Chess/images/Chess_qdt60.png");// black queen
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			
			if (targetCol == preCol || targetRow == preRow) {
				if (isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)) {
					return true;
				}
			}
			
			if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
				if (isValidSquare(targetCol, targetRow)&& !pieceIsOnDiagonalLine(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}
}
