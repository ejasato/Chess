package ui;

import chess.user.Player;

import javax.swing.*;
import java.awt.*;

public class StatsDialog extends JDialog {

    public StatsDialog(Frame parent, Player player) {
        super(parent, "Player Statistics", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        setResizable(false);

        // Header
        JLabel header = new JLabel("Statistics for: " + player.username, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(header, BorderLayout.NORTH);

        // Main content panel
        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ------------------------------------
        // Section: Human vs Human stats
        // ------------------------------------
        addSectionHeader(center, gbc, row++, "Human vs Human (1v1)");
        addStat(center, gbc, row++, "White Wins:", String.valueOf(player.whiteWins));
        addStat(center, gbc, row++, "Black Wins:", String.valueOf(player.blackWins));

        row++; // Spacing

        // ------------------------------------
        // Section: AI mode stats (including Test Mode)
        // ------------------------------------
        addSectionHeader(center, gbc, row++, "Best Wins vs AI (Fewest Moves)");

        // Test Mode (Depth = 1)
        addStat(center, gbc, row++,
                "Test Mode (Depth = 1):",
                formatAiMoves(player.bestTestMoves));

        // Easy (Depth ≤ 5)
        addStat(center, gbc, row++,
                "Easy (Depth ≤ 5):",
                formatAiMoves(player.bestEasyMoves));

        // Medium (Depth ≤ 10)
        addStat(center, gbc, row++,
                "Medium (Depth ≤ 10):",
                formatAiMoves(player.bestMediumMoves));

        // Hard (Depth > 10)
        addStat(center, gbc, row++,
                "Hard (Depth > 10):",
                formatAiMoves(player.bestHardMoves));

        add(center, BorderLayout.CENTER);

        // Close button
        JButton close = new JButton("Close");
        close.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        close.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private String formatAiMoves(int moves) {
        return (moves == Integer.MAX_VALUE ? "No wins yet" : moves + " moves");
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(50, 50, 50));

        panel.add(label, gbc);

        gbc.gridwidth = 1;
    }

    private void addStat(JPanel panel, GridBagConstraints gbc, int row, String key, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(keyLabel, gbc);

        gbc.gridx = 1;

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valLabel.setForeground(new Color(20, 20, 20));
        panel.add(valLabel, gbc);
    }
}
