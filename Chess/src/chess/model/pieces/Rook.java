package chess.model.pieces;


import chess.model.board.Board;

public class Rook extends Piece{
	public Rook(int col, int row, int color) {
		super (col, row, color);
		
		type = Type.ROOK;
		
//		if (color == GamePanel.WHITE) {
//			image = getImage(); // white rook
//		}
//		else {
//			image = getImage();
//		}
//			
	}
	

    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;
        if (tc != col && tr != row) return false;
        if (!canCaptureOrMove(tc, tr, board)) return false;
        return clearStraight(tc, tr, board);
    }
}