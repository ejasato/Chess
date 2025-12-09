package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CapturedPanel extends JPanel {

    private final List<BufferedImage> whiteLost = new ArrayList<>();
    private final List<BufferedImage> blackLost = new ArrayList<>();

    public CapturedPanel() {
        setPreferredSize(new Dimension(120, 8 * 64));
        setBackground(new Color(60, 60, 60));
    }

    public void addCapturedPiece(int color, BufferedImage img) {
        if (img == null) return;
        if (color == 0) {
            // white piece captured
            whiteLost.add(img);
        } else {
            blackLost.add(img);
        }
        repaint();
    }

    public void reset() {
        whiteLost.clear();
        blackLost.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        g.setColor(Color.WHITE);
        g.drawString("Captured", 10, 15);

        int yOffset = 30;
        int size = 24;

        g.drawString("White:", 10, yOffset);
        int x = 10;
        int y = yOffset + 5;
        for (BufferedImage img : whiteLost) {
            g.drawImage(img, x, y, size, size, null);
            x += size + 4;
            if (x + size > getWidth()) {
                x = 10;
                y += size + 4;
            }
        }

        y += size + 16;
        g.drawString("Black:", 10, y);
        x = 10;
        y += 5;
        for (BufferedImage img : blackLost) {
            g.drawImage(img, x, y, size, size, null);
            x += size + 4;
            if (x + size > getWidth()) {
                x = 10;
                y += size + 4;
            }
        }
    }
}

