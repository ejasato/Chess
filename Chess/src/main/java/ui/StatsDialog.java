package ui;

import chess.user.Player;

import javax.swing.*;
import java.awt.*;

public class StatsDialog extends JDialog {

    public StatsDialog(Frame parent, Player p) {
        super(parent, "Player Stats", true);

        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title
        JLabel title = new JLabel("Statistics for " + p.username);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Labels
        JLabel whiteWins = new JLabel("White Wins: " + p.whiteWins);
        JLabel blackWins = new JLabel("Black Wins: " + p.blackWins);

        String bestWhite = (p.bestWhiteWinMoves == Integer.MAX_VALUE)
                ? "None"
                : p.bestWhiteWinMoves + " moves";

        String bestBlack = (p.bestBlackWinMoves == Integer.MAX_VALUE)
                ? "None"
                : p.bestBlackWinMoves + " moves";

        JLabel bestWhiteWin = new JLabel("Best White Win: " + bestWhite);
        JLabel bestBlackWin = new JLabel("Best Black Win: " + bestBlack);

        whiteWins.setFont(new Font("Arial", Font.PLAIN, 14));
        blackWins.setFont(new Font("Arial", Font.PLAIN, 14));
        bestWhiteWin.setFont(new Font("Arial", Font.PLAIN, 14));
        bestBlackWin.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(whiteWins);
        panel.add(blackWins);
        panel.add(bestWhiteWin);
        panel.add(bestBlackWin);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);

        setSize(300, 250);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
