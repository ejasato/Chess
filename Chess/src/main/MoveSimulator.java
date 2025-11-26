package main;

import chess.engine.pieces.*;
//import java.util.ArrayList;

import GUI.Board;

public class MoveSimulator {
	private final BoardState state;
	private final PieceManager pieceManager;
	
	public MoveSimulator(BoardState state, PieceManager manager) {
		this.state = state;
		this.pieceManager = manager;
	}
	
	public void simulate(int mouseX, int mouseY) {
		state.canMove = false;
		state.validSquare = false;
		
		pieceManager.copyToSim();
		
		if (state.castlingP != null) {
			state.castlingP.col = state.castlingP.preCol;
			state.castlingP.x = state.castlingP.getX(state.castlingP.col);
			state.castlingP = null;
		}
		
		Piece active = state.activeP;
		
		if (active == null) {
			return;
		}
		
		active.x = mouseX - Board.HALF_SQUARE_SIZE;
		active.y = mouseY - Board.HALF_SQUARE_SIZE;
		active.col = active.getCol(active.x);
		active.row = active.getRow(active.y);
		
		
		if (active.canMove(active.col, active.row)) {
			state.canMove = true;
			
			if (active.hittingP != null) {
				pieceManager.getsimPieces().remove(active.hittingP.getIndex());
			}
			
			
			checkCastling();
			
			if(!isIllegal(active) && !opponentCanCaptureKing()) {
				state.validSquare = true;
			}
		}
	}
	
	private void checkCastling() {
		if (state.castlingP != null) {
			if (state.castlingP.col == 0) {
				state.castlingP.col += 3;
				
			}
			else if (state.castlingP.col == 7) {
				state.castlingP.col -= 2;
			}
		}
		
		state.castlingP.x = state.castlingP.getX(state.castlingP.col);
	}
	
	public boolean isIllegal(Piece king) {
		if (king.getType() == Type.KING) {
			for (Piece p : pieceManager.getsimPieces()) {
				if (p != king && p.getColor() != king.getColor() && p.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean opponentCanCaptureKing() {
		Piece king = pieceManager.getKing(false, state.getCurrentColor());
		
		if (king == null) {
			return false;
		}
		
		for (Piece p : pieceManager.getsimPieces()) {
			if (p.getColor() != king.getColor() && p.canMove(king.col, king.row)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public boolean isValidKingMove(Piece king, int colPlus, int rowPlus) {
		
		boolean isValid = false;
		
		king.col += colPlus;
		king.row += rowPlus;
		
		if(king.canMove(king.col, king.row)) {
			if (king.hittingP != null) {
				pieceManager.getsimPieces().remove(king.hittingP.getIndex());
			}
			
			if (!isIllegal(king)) {
				isValid = true;
			}
		}
		king.resetPosition();
		pieceManager.copyToSim();
		
		return isValid;
	}
	 
}
