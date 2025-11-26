package main;

import chess.engine.pieces.*;
import java.util.ArrayList;

import GUI.Board;

public class PromotionHandler {
	
	private final BoardState state;
	private final PieceManager pieceManager;
	
	private final ArrayList<Piece> promoPieces = new ArrayList<>();
	
	public PromotionHandler(BoardState state, PieceManager manager) {
		this.state = state;
		this.pieceManager = manager;
	}
	
	public boolean canPromote() {
		Piece active = state.activeP;
		if (active == null) {
			return false;
		}
		if (active.getType() == Type.PAWN) {
			if ((state.getCurrentColor() == BoardState.WHITE && active.row == 0) ||
					(state.getCurrentColor() == BoardState.BLACK && active.row == 7)) {
				promoPieces.clear();
				promoPieces.add(new Rook(9,2, state.getCurrentColor()));
				promoPieces.add(new Knight(9,3, state.getCurrentColor()));
				promoPieces.add(new Bishop(9,4, state.getCurrentColor()));
				promoPieces.add(new Queen(9,5, state.getCurrentColor()));
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Piece> getPromoPieces(){
		return promoPieces;
	}
	
	public boolean tryPromote(int mouseX, int mouseY) {
		if (!state.promotion) {
			return false;
		}
	
		
		for (Piece p : promoPieces) {
			if (p.col == mouseX/Board.SQUARE_SIZE && p.row == mouseY/Board.SQUARE_SIZE) {
				switch (p.getType()) {
				case ROOK:
					pieceManager.getsimPieces().add(new Rook(state.activeP.col, state.activeP.row, state.getCurrentColor()));
					break;
				case KNIGHT:
					pieceManager.getsimPieces().add(new Knight(state.activeP.col, state.activeP.row, state.getCurrentColor()));
					break;
				case BISHOP:
					pieceManager.getsimPieces().add(new Bishop(state.activeP.col, state.activeP.row, state.getCurrentColor()));
					break;
				case QUEEN:
					pieceManager.getsimPieces().add(new Queen(state.activeP.col, state.activeP.row, state.getCurrentColor()));
					break;
				default:
					break;
				}
				pieceManager.getsimPieces().remove(state.activeP.getIndex());
				pieceManager.copyFromSim();
				
				state.activeP = null;
				state.promotion = false;
				
				state.flipCurrentColor();
				pieceManager.resetTwoStepFor(state.getCurrentColor());
				return true;
			}
		}
		return false;
	}
	
	
		
}