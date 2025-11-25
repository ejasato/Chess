package chess.engine.pieces;

public class Pawn extends Piece{
	
	public Pawn(int col, int row, int color) {
		super(col, row, color);
		
		Type type = Type.PAWN;
		
		// if (color == GamePanel.WHITE) {
		// 	image = getImage(); // white pawn
		// }
		// else {
		// 	image = getImage(); // black pawn
		// }
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			
			int moveValue;
			
			if (color == GamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			
			Piece hittingP = getHittingP(targetCol, targetRow);
			
			// 1 square
			if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null ) {
				return true;
			}
			
			//2 square
			if (targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && moved == false
					&& pieceIsOnStraightLine(targetCol, targetRow) == false ) {
			}
					
					
			// diagonal movement & capture move
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null 
					&& hittingP.color!=color) {
				return true;
			}
					
			// En Passant
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + movaValue) {
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == targetCol && piece.row == preRow && piece.twoStepped == true) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
