package chess.engine.pieces;

public class Piece {

	protected Type type;
	protected int x, y;
	protected int col, row, preCol, preRow;
	protected int color;
	protected Piece toCapture;
	//protected Image image;
	
	// constructor
	public Piece(int col, int row, int color) {
		this.col = col;
		this.row = row;
		this.color = color;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}
	
	// Image display for a piece
	// public Image getImage(String imagePath) {
	// 	Image image = null;
		
	// }
	
	public int getX(int col) {
		return // return current piece position in the x
	}
	public int getY(int row) {
		return // return current piece position in the y
	}
	
	public int getCol(int x) {
		return 
	}
	
	public int getRow(int y) {
		
	}
	
	public Piece getHittingP(int toCaptureCol, int toCaptureRow) {
		for (Piece piece )
	}
	
	public int getIndex() {
		for in
	}
	
	public void draw() {
		
	}
	
	public void updatePosition() {
		
		
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		
	}
	
	public boolean isWithinBoard(int targetCol, int targetRow) {
		
	}
	
	public void resetPosition() {
		
	}
	
	public boolean isValidSquare(int targetCol, int targetRow) {
		Piece hittingP = getHittingP(targetCol, targetRow);
		if (hittingP == null) {
			return true
		}
	}
	
	public boolean isSameSquare(int targetCol, int targetRow) {
		
		
	}
	
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
		
	}
	
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
		
	}
	
}
