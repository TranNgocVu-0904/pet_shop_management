package view;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new GridLayout(2, 2, 30, 30));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        add(createCard("📦 Total Products", "0"));
        add(createCard("🐾 Total Pets", "0"));
        add(createCard("💰 Total Revenue", "$0.00"));
        add(createCard("🧾 Total Orders", "0"));
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(45, 45, 45));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        card.setPreferredSize(new Dimension(200, 200));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.CYAN);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
}
