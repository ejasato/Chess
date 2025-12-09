package chess.model.pieces;

import chess.model.board.Board;

public class Queen extends Piece{
	public Queen(int col, int row, int color) {
		super(col,row, color);
		
		type = Type.QUEEN;
		
//		if (color == GamePanel.WHITE) {
//			image = getImage(); // white queen
//		}
//		else {
//			image = getImage();// black queen
//		}
	}
	
    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;
        if (!canCaptureOrMove(tc, tr, board)) return false;

        return clearStraight(tc, tr, board) ||
               clearDiagonal(tc, tr, board);
    }
    @Override
    public Piece copy() {
        Rook r = new Rook(col, row, color);
        r.setTwoStepped(this.twoStepped);
        return r;
    }

}