package main;

import chess.engine.pieces.*;
import java.util.ArrayList;

public class PieceManager {
	private final ArrayList<Piece> pieces;
	private final ArrayList<Piece> simPieces;
	
	public PieceManager(ArrayList<Piece> piecesRef, ArrayList<Piece> simPiecesRef) {
		this.pieces = piecesRef;
		this.simPieces = simPiecesRef;
	}
	
	public void setPieces() {
		pieces.clear();
		
		pieces.add(new Pawn(0,6, BoardState.WHITE));
		pieces.add(new Pawn(1,6, BoardState.WHITE));
		pieces.add(new Pawn(2,6, BoardState.WHITE));
		pieces.add(new Pawn(3,6, BoardState.WHITE));
		pieces.add(new Pawn(4,6, BoardState.WHITE));
		pieces.add(new Pawn(5,6, BoardState.WHITE));
		pieces.add(new Pawn(6,6, BoardState.WHITE));
		pieces.add(new Pawn(7,6, BoardState.WHITE));
		pieces.add(new Rook(0,7, BoardState.WHITE));
		pieces.add(new Rook(7,7, BoardState.WHITE));
		pieces.add(new Knight(1,7, BoardState.WHITE));
		pieces.add(new Knight(6,7, BoardState.WHITE));
		pieces.add(new Bishop(2,7, BoardState.WHITE));
		pieces.add(new Bishop(5,7, BoardState.WHITE));
		pieces.add(new Queen(3,7, BoardState.WHITE));
		pieces.add(new King(4,7, BoardState.WHITE));
		
		pieces.add(new Pawn(0,1, BoardState.BLACK));
		pieces.add(new Pawn(1,1, BoardState.BLACK));
		pieces.add(new Pawn(2,1, BoardState.BLACK));
		pieces.add(new Pawn(3,1, BoardState.BLACK));
		pieces.add(new Pawn(4,1, BoardState.BLACK));
		pieces.add(new Pawn(5,1, BoardState.BLACK));
		pieces.add(new Pawn(6,1, BoardState.BLACK));
		pieces.add(new Pawn(7,6, BoardState.BLACK));
		pieces.add(new Rook(0,0, BoardState.BLACK));
		pieces.add(new Rook(7,0, BoardState.BLACK));
		pieces.add(new Knight(1,0, BoardState.BLACK));
		pieces.add(new Knight(6,0, BoardState.BLACK));
		pieces.add(new Bishop(2,0, BoardState.BLACK));
		pieces.add(new Bishop(5,0, BoardState.BLACK));
		pieces.add(new Queen(3,0, BoardState.BLACK));
		pieces.add(new King(4,0, BoardState.BLACK));
		
	}
	
	public void copyToSim() {
		simPieces.clear();
		for (Piece p : pieces) {
			simPieces.add(p);
		}
	}
	
	public void copyFromSim() {
		pieces.clear();
		for (Piece p : simPieces) {
			pieces.add(p);
			
		}
		
	}
	
	public ArrayList<Piece> getPieces(){
		return pieces;
	}
	
	public ArrayList<Piece> getsimPieces(){
		return simPieces;
	}
	
	public void resetTwoStepFor(int color) {
		for (Piece p : pieces) {
			if (p.getColor() == color) {
				p.setTwoStepped(false);
			}
		}
	}
	
	public Piece getKing(boolean opponent, int currentColor) {
		for (Piece p : simPieces) {
			if(p.getType() == Type.KING) {
				if (opponent && p.getColor() != currentColor) {
					return p;
					
				}
				if (!opponent && p.getColor() == currentColor) {
					return p;
				}
			}
		}
		return null;
	}
}
