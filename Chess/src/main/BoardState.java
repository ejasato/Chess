package main;

import chess.engine.pieces.*;
import java.util.ArrayList;

public class BoardState {
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	public final ArrayList<Piece> pieces = new ArrayList<>();
	public final ArrayList<Piece> simPieces = new ArrayList<>();
	
	public Piece castlingP = null;
	public Piece activeP = null;
	public Piece checkingP = null;
	
	public boolean canMove = false;
	public boolean validSquare = false ;
	public boolean promotion = false;
	public boolean gameOver = false;
	public boolean stalemate = false;
	
	private int currentColor = WHITE;
		
	public int getCurrentColor() {
		return currentColor;
		
	}
	
	public void setCurrentColor(int color) {
		currentColor = color;
	}
	
	public void flipCurrentColor(){
		currentColor = (currentColor == WHITE) ? BLACK : WHITE;
	}
}
