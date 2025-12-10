//package chess.model.pieces;
//
//import chess.model.board.Board;
//
//public class Pawn extends Piece{
//	
//	public Pawn(int col, int row, int color) {
//		super(col, row, color);
//		
//		type = Type.PAWN;
//		
//		// if (color == GamePanel.WHITE) {
//		// 	image = getImage(); // white pawn
//		// }
//		// else {
//		// 	image = getImage(); // black pawn
//		// }
//	}
//	
//	@Override
//	public boolean canMove(int tc, int tr, Board board) {
//	    if (!inside(tc, tr)) return false;
//
//	    int dir = (color == 0 ? -1 : 1); // white up, black down
//	    int startRow = (color == 0 ? 6 : 1);
//
//	    // ---- Standard forward move ----
//	    if (tc == col && tr == row + dir) {
//	        return board.getPiece(tc, tr) == null;
//	    }
//
//	    // ---- Double step ----
//	    if (tc == col && tr == row + 2*dir && row == startRow) {
//	        return board.getPiece(tc, row + dir) == null &&
//	               board.getPiece(tc, tr) == null;
//	    }
//
//	    // ---- Normal capture ----
//	    if (Math.abs(tc - col) == 1 && tr == row + dir) {
//	        Piece target = board.getPiece(tc, tr);
//	        if (target != null && target.color != this.color) {
//	            return true;
//	        }
//
//	        // ---- En Passant capture check: handled in MoveValidator ----
//	        // Allow diagonal move into empty square so validator can finalize EP.
////	        Piece lastMoved = board.lastMovedPiece;
////	        if (lastMoved != null && lastMoved.getType() == Type.PAWN) {
////	            // The pawn must be next to us
////	            if (lastMoved.row == row && lastMoved.col == tc) {
////	                // Pawn must have just moved two squares
////	                if (Math.abs(lastMoved.startRow - lastMoved.row) == 2) {
////	                    return true;  // Let validator confirm legality
////	                }
////	            }
////	        }
//	    }
//
//	    return false;
//	}
//
//    @Override
//    public Piece copy() {
//        Pawn p = new Pawn(col, row, color);
//        p.setTwoStepped(this.twoStepped);
////        p.setHasMoved(this.hasMoved());
//        return p;
//    }
//
//
//}

package chess.model.pieces;

import chess.model.board.Board;

public class Pawn extends Piece {

    public Pawn(int col, int row, int color) {
        super(col, row, color);
        type = Type.PAWN;
    }

    @Override
    public boolean canMove(int tc, int tr, Board board) {
        if (!inside(tc, tr)) return false;

        int dir = (color == 0 ? -1 : 1); // WHITE (0) moves up (row--), BLACK down (row++)
        int startRow = (color == 0 ? 6 : 1);

        // --- Single step
        if (tc == col && tr == row + dir) {
            return board.getPiece(tc, tr) == null;
        }

        // --- Double step
        if (tc == col && tr == row + 2 * dir && row == startRow) {
            return board.getPiece(tc, row + dir) == null &&
                   board.getPiece(tc, tr) == null;
        }

        // --- Normal capture
        if (Math.abs(tc - col) == 1 && tr == row + dir) {
            Piece target = board.getPiece(tc, tr);
            return target != null && target.color != this.color;
        }

        // En passant is handled in MoveValidator + Board, not here
        return false;
    }

    @Override
    public Piece copy() {
        Pawn p = new Pawn(col, row, color);
        p.setTwoStepped(this.twoStepped);
        return p;
    }
}

