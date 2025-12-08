package chess.model.pieces;

import chess.model.board.Board;

public class Bishop  extends Piece{
	public Bishop(int col,int row, int color) {
		super(col, row, color);
		
		type = Type.BISHOP;
		
		// if (color == Game.WHITE){
		// 	image = getImage(); //white bishop image
		// }
		// else {
		// 	image = getImage();//black bishop image
		// }
	}
	

    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;
        if (!canCaptureOrMove(tc, tr, board)) return false;
        return clearDiagonal(tc, tr, board);
    }
	
}