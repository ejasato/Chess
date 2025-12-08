package chess.model.pieces;

import chess.model.board.Board;

public class Knight extends Piece{
	
	public Knight(int col, int row, int color) {
		super (col, row, color);
		
		type = Type.KNIGHT;
		
		// if (color == GamePanel.WHITE) {
		// 	image = getImage(); // white knight
		// }
		// else {
		// 	image = getImage(); // black knight
		// }
		
	}
	

    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;

        int dc = Math.abs(tc - col);
        int dr = Math.abs(tr - row);

        if (dc * dr == 2) {
            return canCaptureOrMove(tc, tr, board);
        }
        return false;
    }
	
	
}