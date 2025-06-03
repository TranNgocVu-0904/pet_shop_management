package view;

import controller.AuthController;
import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel contentPanel = new JPanel(layout);

    public HomeFrame(boolean isManager) {
        setTitle("Petshop - Home");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // ===== Sidebar =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 236, 236));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));

//        JLabel title = new JLabel("☰ Menu", SwingConstants.CENTER);
//        title.setForeground(new Color(0x476E91));
//        title.setFont(new Font("SansSerif", Font.BOLD, 22));
//        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
//        title.setAlignmentX(Component.CENTER_ALIGNMENT);
//        sidebar.add(title);

        // inside HomeFrame constructor
        // Replace title:
        sidebar.add(Box.createVerticalStrut(30));
        JButton profileBtn = new JButton("👤 Profile");
        profileBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        profileBtn.setMaximumSize(new Dimension(180, 45));
        profileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileBtn.setFocusPainted(false);
        profileBtn.setBackground(new Color(0x007BFF));
        profileBtn.setForeground(Color.WHITE);
        profileBtn.addActionListener(e -> {
            new ProfileDialog(this);  
        });
        sidebar.add(profileBtn);

        // Show hello username
        JLabel helloUser = new JLabel("Hello " + AuthController.currentUser.getUsername(), SwingConstants.CENTER);
        helloUser.setForeground(new Color(0x476E91));
        helloUser.setFont(new Font("SansSerif", Font.BOLD, 14));
        helloUser.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        helloUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(helloUser);

    

        // ===== Navigation Buttons =====
        addNavButton(sidebar, "Home", "home");
        addNavButton(sidebar, "Pets", "pet");
        addNavButton(sidebar, "Products", "product");
        addNavButton(sidebar, "Customers", "customer");
        if (isManager) addNavButton(sidebar, "Staff", "staff");
        addNavButton(sidebar, "Bill", "bill");

        // Spacer
        sidebar.add(Box.createVerticalGlue());

        JButton reloadBtn = new JButton("Reload");
        reloadBtn.setBackground(new Color(0x28a745)); // Green-ish
        reloadBtn.setForeground(Color.WHITE);
        reloadBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        reloadBtn.setMaximumSize(new Dimension(180, 45));
        reloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);  
        reloadBtn.setFocusPainted(false);

        // 🔁 Logic to reload UI
        reloadBtn.addActionListener(e -> {
            dispose(); // Close current window
            new HomeFrame(AuthController.isManager()); // Reopen HomeFrame with same role
        });

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reloadBtn);

        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(0xFF837D)); // RED
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutBtn.setMaximumSize(new Dimension(180, 45));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(30));

        // ===== Content =====
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new PetPanel(), "pet");
        contentPanel.add(new ProductPanel(), "product");
        contentPanel.add(new CustomerPanel(), "customer");
        contentPanel.add(new StaffPanel(), "staff");
        contentPanel.add(new BillingPanel(), "bill");

        layout.show(contentPanel, "home");

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addNavButton(JPanel sidebar, String name, String panelKey) {
        JButton btn = new JButton(name);
        btn.setMaximumSize(new Dimension(180, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0x7DC2FF));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setMargin(new Insets(20, 20, 20, 20));
        btn.addActionListener(e -> layout.show(contentPanel, panelKey));

        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(5));
    }
}
