package view;

import controller.AuthController;
import model.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    
    public LoginFrame() {
        setTitle("Petshop Login");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🐾 Top Panel: Logo
        JLabel logo = new JLabel();
        ImageIcon logoIcon = new ImageIcon("src/Images/shopping-cart(2).png"); // <-- Replace with your logo path
        logo.setIcon(new ImageIcon(logoIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH)));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        // 🧾 Center Panel: Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.BLACK);

        JLabel title = new JLabel("Sign in");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JTextField emailField = new JTextField("Email address");
        JPasswordField passwordField = new JPasswordField("Password");
        JButton signInButton = new JButton("SIGN IN");
        JLabel orLabel = new JLabel("OR");
        JButton signUpButton = new JButton("SIGN UP");

        // 📦 Styling fields
        JTextField[] fields = { emailField, passwordField };
        for (JTextField field : fields) {
            field.setMaximumSize(new Dimension(300, 40));
            field.setForeground(Color.GRAY);
            field.setBackground(Color.DARK_GRAY);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        // 🔒 Mask password and handle placeholder
        passwordField.setEchoChar((char) 0); // Disable masking initially
        addPlaceholderBehavior(emailField, "Email address");
        addPlaceholderBehavior(passwordField, "Password", true);

        // 🎨 Button style
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.setBackground(new Color(139, 0, 0));
        signInButton.setForeground(Color.WHITE);
        signInButton.setFocusPainted(false);
        signInButton.setMaximumSize(new Dimension(300, 40));

        orLabel.setForeground(Color.LIGHT_GRAY);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setBackground(Color.GRAY);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setMaximumSize(new Dimension(300, 40));

        // ➕ Add components
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(title);
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(signInButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(orLabel);
        formPanel.add(signUpButton);

        add(logo, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // ✅ Events
        signInButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            boolean success = authController.login(email, password);
            if (success) {
                dispose();
                boolean isManager = AuthController.currentUser instanceof Manager;
                new HomeFrame(isManager);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        
//        signInButton.addActionListener(e -> {
//            new HomeFrame(isManager);
//        });

        signUpButton.addActionListener(e -> {
            new SignupFrame();
        });

        setVisible(true);
    }

    // 🧠 Utility for placeholder behavior
    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        addPlaceholderBehavior(field, placeholder, false);
    }

    private void addPlaceholderBehavior(JTextField field, String placeholder, boolean isPassword) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    if (isPassword && field instanceof JPasswordField pf) {
                        pf.setEchoChar('•'); // show bullets on focus
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (isPassword && field instanceof JPasswordField pf) {
                        pf.setEchoChar((char) 0); // hide bullets when showing placeholder
                    }
                }
            }
        });
    }
}
