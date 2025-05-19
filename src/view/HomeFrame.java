package view;

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

        // ======= Sidebar =======
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel title = new JLabel("☰ Menu", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        // ======= Navigation Buttons =======
        sidebar.add(createNavButton("Home", "home"));
        sidebar.add(createNavButton("Pets", "pet"));
        sidebar.add(createNavButton("Products", "product"));
        sidebar.add(createNavButton("Customers", "customer"));
        if (isManager) {
            sidebar.add(createNavButton("Staff", "staff"));
        }

        // ======= Spacer + Logout Button =======
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setMaximumSize(new Dimension(180, 40));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(); // ✅ Go back to login
        });

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // ======= Content Area =======
        contentPanel.setBackground(Color.BLACK);
        contentPanel.add(new HomePanel(), "home");
        contentPanel.add(new PetPanel(), "pet"); 
        contentPanel.add(new ProductPanel(), "product");
        contentPanel.add(new CustomerPanel(), "customer");
        contentPanel.add(new StaffPanel(), "staff");

        layout.show(contentPanel, "home");

        // ======= Add to Frame =======
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createNavButton(String name, String panel) {
        JButton btn = new JButton(name);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(Color.GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btn.addActionListener(e -> layout.show(contentPanel, panel));
        return btn;
    }
}
