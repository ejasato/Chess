package ui;

import javax.swing.*;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final JTextArea textArea;
    private int moveNumber = 1;

    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 8 * 64));

        JLabel title = new JLabel("Moves");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(textArea);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void addMove(String move) {
        // Very simple: one move per line, with move number
        textArea.append(moveNumber + ". " + move + "\n");
        moveNumber++;
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void reset() {
        textArea.setText("");
        moveNumber = 1;
    }
}

