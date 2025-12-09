package ui;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private final ChessPanel boardPanel;
    private final JLabel statusLabel;

    public GameFrame() {
        setTitle("Eric's Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        boardPanel = new ChessPanel(statusLabel);

        // Optional side panels if you already have them
        MoveHistoryPanel historyPanel = new MoveHistoryPanel();
        CapturedPanel capturedPanel   = new CapturedPanel();

        boardPanel.setHistoryPanel(historyPanel);
        boardPanel.setCapturedPanel(capturedPanel);

        // Top menu
        setJMenuBar(createMenuBar());

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(historyPanel, BorderLayout.EAST);
        add(capturedPanel, BorderLayout.WEST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        // Game menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> boardPanel.newGame());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        // Mode menu
        JMenu modeMenu = new JMenu("Mode");
        JRadioButtonMenuItem pvpItem = new JRadioButtonMenuItem("1 vs 1", false);
        JRadioButtonMenuItem pvcItem = new JRadioButtonMenuItem("Vs Computer", true);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvpItem);
        modeGroup.add(pvcItem);

        pvpItem.addActionListener(e -> {
            boardPanel.setVsComputer(false);
            boardPanel.newGame();
        });
        pvcItem.addActionListener(e -> {
            boardPanel.setVsComputer(true);
            boardPanel.newGame();
        });

        modeMenu.add(pvpItem);
        modeMenu.add(pvcItem);

        // Difficulty menu
        JMenu diffMenu = new JMenu("Difficulty");
        JRadioButtonMenuItem easy   = new JRadioButtonMenuItem("Easy (Depth 5)", false);
        JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium (Depth 10)", true);
        JRadioButtonMenuItem hard   = new JRadioButtonMenuItem("Hard (Depth 20)", false);

        ButtonGroup diffGroup = new ButtonGroup();
        diffGroup.add(easy);
        diffGroup.add(medium);
        diffGroup.add(hard);

        easy.addActionListener(e -> boardPanel.setAIDepth(5));
        medium.addActionListener(e -> boardPanel.setAIDepth(10));
        hard.addActionListener(e -> boardPanel.setAIDepth(20));

        diffMenu.add(easy);
        diffMenu.add(medium);
        diffMenu.add(hard);

        bar.add(gameMenu);
        bar.add(modeMenu);
        bar.add(diffMenu);

        return bar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}

//public static void main(String[] args) {
//    SwingUtilities.invokeLater(GameFrame::new);
//}

