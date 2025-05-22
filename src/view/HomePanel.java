package view;

import controller.ProductController;
import controller.PetController;
import controller.CustomerController;
import controller.BillingController; // You'll need to create this if not available

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new GridLayout(2, 2, 50, 50));
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(90, 90, 90, 90));

        // Fetch values
        int totalProducts = ProductController.getAllProducts().size();
        int totalPets = PetController.getAllPets().size();
//        int totalOrders = BillController.getAllBills().size(); // You'll need to implement this method
//        BigDecimal totalRevenue = BillController.getTotalRevenue(); // Implement this as well

        // Add cards
        add(createCard("📦 Total Products", String.valueOf(totalProducts)));
        add(createCard("🐾 Total Pets", String.valueOf(totalPets)));
        add(createCard("💰 Total Revenue", "$0.00"));
        add(createCard("🧾 Total Orders", "0"));
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        card.setPreferredSize(new Dimension(70, 70)); // smaller size

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0x476E91));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(new Color(0x9CFF7D));
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 80));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
}
