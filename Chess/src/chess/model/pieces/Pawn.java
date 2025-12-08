package chess.model.pieces;

import chess.model.board.Board;

public class Pawn extends Piece{
	
	public Pawn(int col, int row, int color) {
		super(col, row, color);
		
		type = Type.PAWN;
		
		// if (color == GamePanel.WHITE) {
		// 	image = getImage(); // white pawn
		// }
		// else {
		// 	image = getImage(); // black pawn
		// }
	}
	
    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;

        int dir = (color == 0 ? -1 : 1); // white up, black down
        int startRow = (color == 0 ? 6 : 1);

        // --- Single step
        if (tc == col && tr == row + dir) {
            return board.getPiece(tc, tr) == null;
        }

        // --- Double step
        if (tc == col && tr == row + 2 * dir && row == startRow) {
            return board.getPiece(tc, row + dir) == null &&
                   board.getPiece(tc, tr) == null;
        }

        // --- Capture
        if (Math.abs(tc - col) == 1 && tr == row + dir) {
            Piece target = board.getPiece(tc, tr);
            return target != null && target.color != this.color;
        }

        // --- En passant will be handled by validator → always return false here
        return false;
	}
}
