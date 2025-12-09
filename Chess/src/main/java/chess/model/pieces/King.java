package chess.model.pieces;

import chess.model.board.Board;

public class King extends Piece{
	
	public King(int col, int row, int color) {
		super(col, row, color);
		
		type = Type.KING;
		
		// if (color == GamePanel.White) {
		// 	Image image = getImage();  // white king
		// }
		// else {
		// 	Image image = getImage(); // black king
		// }
	}
	
    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;

        int dc = Math.abs(tc - col);
        int dr = Math.abs(tr - row);

        if (dc <= 1 && dr <= 1) {
            return canCaptureOrMove(tc, tr, board);
        }

        // castling added later if needed
        return false;
    }
    @Override
    public Piece copy() {
        Rook r = new Rook(col, row, color);
        r.setTwoStepped(this.twoStepped);
        return r;
    }

}
