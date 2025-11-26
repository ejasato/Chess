package main;

import java.awt.*;
import chess.engine.pieces.*;
import java.util.List;

import GUI.Board;

public class RenderHelper {
	public static void drawPromotionChoices(Graphics2D g2,List<Piece> promoPieces) {
		for (Piece p : promoPieces) {
			g2.drawImage(p.image, p.getX(p.col), p.getY(p.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
		}
	}
	public static void drawSemiTransparentOverlay(Graphics2D g2, Color color, float alpha) {
		
		g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(alpha*255)));
		g2.fillRect(0, 0, Board.SQUARE_SIZE * 8 , Board.SQUARE_SIZE * 8);
	}
}
