package ui;

import javax.swing.*;
import java.awt.*;

public class StartMenuDialog extends JDialog {

    public interface MenuSelectionListener {
        void onHumanVsHuman();
        void onVsComputerWhite();
        void onVsComputerBlack();
        void onResume();  // for ESC pause menu
    }

    public StartMenuDialog(JFrame parent, boolean showResume, MenuSelectionListener listener) {
        super(parent, "Game Menu", true); // modal = true
        setLayout(new BorderLayout());

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0, 1, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (showResume) {
            JButton resumeBtn = new JButton("Resume Game");
            resumeBtn.addActionListener(e -> {
                listener.onResume();
                dispose();
            });
            buttons.add(resumeBtn);
        }

        JButton h2hBtn = new JButton("Human vs Human");
        h2hBtn.addActionListener(e -> {
            listener.onHumanVsHuman();
            dispose();
        });

        JButton aiWhiteBtn = new JButton("Vs Computer (Human as White)");
        aiWhiteBtn.addActionListener(e -> {
            listener.onVsComputerWhite();
            dispose();
        });

        JButton aiBlackBtn = new JButton("Vs Computer (Human as Black)");
        aiBlackBtn.addActionListener(e -> {
            listener.onVsComputerBlack();
            dispose();
        });

        buttons.add(h2hBtn);
        buttons.add(aiWhiteBtn);
        buttons.add(aiBlackBtn);

        add(buttons, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
}
