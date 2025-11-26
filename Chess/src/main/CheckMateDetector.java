package main;

import chess.engine.pieces.*;
//import java.util.ArrayList;

public class CheckMateDetector {
	private final BoardState state;
	private final PieceManager pieceManager;
	private final MoveSimulator simulator;
	
	public CheckMateDetector(BoardState state, PieceManager manager, MoveSimulator sim) {
		this.state = state;
		this.pieceManager = manager;
		this.simulator = sim;
	}
	
	public boolean isKingInCheck() {
		Piece king = pieceManager.getKing(true, state.getCurrentColor());
		
		if (king == null) {
			return false;
		}
		
		if(state.checkingP != null && state.activeP.canMove(king.col, king.row)) {
			state.checkingP = state.activeP;
			return true;
			
		}
		
		else {
			state.checkingP = null;
		}
		
		return false;
	}
	
	
	public boolean isCheckMate() {
		Piece king = pieceManager.getKing(true, state.getCurrentColor());
		
		if (king == null) {
			return false;
		}
		
		if (kingCanMove(king)) {
			return false;
			
		}
		
		Piece checking = state.checkingP;
		
		if(checking == null) {
			return false;
		}
		
		int colDiff = Math.abs(checking.col - king.col);
		int rowDiff = Math.abs(checking.row = king.row);
		
		if(colDiff == 0) {
			if (checking.row < king.row) {
				for(int row = checking.row; row < king.row; row ++) {
					for (Piece p : pieceManager.getsimPieces()) {
						if (p != king && p.getColor() != state.getCurrentColor() &&
								p.canMove(checking.col, row)) {
							return false;
						}
					}
				}
			}
			else {
				for (int row = checking.row; row > king.row; row--) {
					for (Piece p : pieceManager.getsimPieces()) {
						if (p != king && p.getColor() != state.getCurrentColor() &&
								p.canMove(checking.col, row)) {
							return false;
						}
					}
				}
			}
		}
		else if (rowDiff == 0) {
			if (checking.col < king.col) {
				for (int col = checking.col; col < king.col ; col++) {
					for (Piece p : pieceManager.getsimPieces()) {
						if (p != king && p.getColor() != state.getCurrentColor() &&
								p.canMove(col, checking.row)) {
							return false;
						}
					}
				}
			}
			else {
				for (int col = checking.col; col > king.col; col --) {
					for (Piece p : pieceManager.getsimPieces()) {
						if (p != king && p.getColor() != state.getCurrentColor() &&
								p.canMove(col, checking.row)) {
							return false;
						}
					}
				}
			}
		}
		
		else if (colDiff == rowDiff) {
			int dc = (checking.col < king.col) ? 1 : -1;
			int dr = (checking.row < king.row) ? 1 : -1;
			int col = checking.col, row = checking.row;
			
			while (col != king.col && row != king.row) {
				for (Piece p : pieceManager.getsimPieces()) {
					if (p != king && p.getColor() != state.getCurrentColor() &&
							p.canMove(col, row)){
						return false;
					}
				}
				col += dc;
				row += dr;
			}
			
		}
		else {
			for (Piece p : pieceManager.getsimPieces()) {
				if (p != king && p.getColor() != state.getCurrentColor() && 
						p.canMove(checking.col, checking.row)) {
					return false;
				}
			}
		}
		return true;
		
	}
	
	private boolean kingCanMove(Piece king) {
		if (simulator.isValidKingMove(king, -1, -1)) {
			return true;
		}
		if (simulator.isValidKingMove(king, 0, -1)) {
			return true;
		}
		if (simulator.isValidKingMove(king, 1, -1)) {
			return true;
		}
		if (simulator.isValidKingMove(king, -1, 0)) {
			return true;
		}
		if (simulator.isValidKingMove(king, 1, 0)) {
			return true;
		}
		if (simulator.isValidKingMove(king, -1, 1)) {
			return true;
		}
		if (simulator.isValidKingMove(king, 0, 1)) {
			return true;
		}
		if (simulator.isValidKingMove(king, 1, 1)) {
			return true;
		}
		return false;
	}
	
	public boolean isStalemate() {
		int count = 0;
		for (Piece p : pieceManager.getsimPieces()) {
			if (p.getColor() != state.getCurrentColor()) {
				count ++;
			}
		}
		if (count == 1) {
			return !kingCanMove(pieceManager.getKing(true, state.getCurrentColor()));
		}
		return false;
	}
}
