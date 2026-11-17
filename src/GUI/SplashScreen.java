package GUI;

import Service.ExpenseService;

import javax.swing.*;
import java.awt.*;

public class SplashScreen {

    public static void showSplash() {
        JWindow window = new JWindow();
        window.setSize(450, 280);
        window.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel title = new JLabel("Expense Tracker", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage your money, simply.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 220, 255));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        window.add(panel);
        window.setVisible(true);

        Timer timer = new Timer(1800, e -> {
            window.dispose();
            ExpenseService service = new ExpenseService();
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
        timer.setRepeats(false);
        timer.start();
    }
}