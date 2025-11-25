package chess.engine.pieces;

public class King extends Piece{
	
	public King(int col, int row, int color) {
		super(col, row, color);
		
		Type type = Type.KING;
		
		// if (color == GamePanel.White) {
		// 	Image image = getImage();  // white king
		// }
		// else {
		// 	Image image = getImage(); // black king
		// }
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if (isWithinBoard(targetCol, targetRow)) {
			if (Math.abs(targetCol - preCol) +
					Math.abs(targetRow - preRow) ==1 || 
				Math.abs(targetCol- preCol) * Math.abs(targetRow - preRow) == 1) {
				if (isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
			// castling
			if (!moved) {
				
				// Right Castling
				if (targetCol == preCol + 2 && target Row == preRow &&
						!pieceIsOnStraightLine(targetCol, targetRow)) {
					for (Piece piece : GamePanel.simPieces) {
						if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
							GamePanel.castlingP = piece;
							return true;
						}
					}
				}
				
				// Left Castling
				
				if (targetCol == preCol + 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
					Piece[] p = new Piece[2];
					for (Piece piece : GamePanel.simPieces) {
						if (piece.col == preCol - 3 && piece.row == targetRow) {
							p[0] = piece;
							
						}
						if (piece.col == preCol- 4&& piece.row == targetRow) {
							p[1] = piece;
							
						}
						if (p[0] == null && p[1] != null && !p[1].moved) {
							GamePanel.castlingP = p[1];
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}
}
