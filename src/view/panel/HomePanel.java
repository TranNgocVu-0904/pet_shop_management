package view.panel;

import controller.product.ProductController;
import controller.pet.PetController;
import controller.bill.BillingController;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class HomePanel extends JPanel {

    public HomePanel()
    {
        setLayout(new GridLayout(2, 2, 50, 50));
        
        setBackground(new Color(240, 236, 236));
        
        setBorder(BorderFactory.createEmptyBorder(90, 90, 90, 90));

        // Controllers (for proper non-static access)
        BillingController billing = new BillingController();

        // === Fetch data ===
        ProductController productController = new ProductController();
        PetController petController = new PetController();

        int totalProducts = productController.getAllProducts().size();
        int totalPets = petController.getAllPets().size();
        
        int totalOrders = billing.getTotalOrders();          // You already fixed this
        BigDecimal totalRevenue = billing.getTotalRevenue();     // Also fixed

        // === Add info cards ===
        add(createCard("📦 Total Products", String.valueOf(totalProducts)));
        add(createCard("🐾 Total Pets", String.valueOf(totalPets)));
        add(createCard("💰 Total Revenue", "$" + totalRevenue));
        add(createCard("🧾 Total Orders", String.valueOf(totalOrders)));
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        card.setPreferredSize(new Dimension(150, 150));

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
