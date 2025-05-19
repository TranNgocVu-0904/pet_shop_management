package view;

import controller.AuthController;
import model.Staff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;

public class SignupFrame extends JFrame {
    private final AuthController authController = new AuthController();

    public SignupFrame() {
        setTitle("Petshop - Sign Up");
        setSize(600, 600); // ✅ Larger frame
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🔼 Optional logo
        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon("src/view/petshop_logo.png").getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH)));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        // 📋 Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.BLACK);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80)); // ⬅ padding all around

        JLabel title = new JLabel("Create Staff Account");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // ✏️ Input Fields
        JTextField nameField = new JTextField("Name");
        JTextField emailField = new JTextField("Email");
        JTextField phoneField = new JTextField("Phone");
        JTextField usernameField = new JTextField("Username");
        JPasswordField passwordField = new JPasswordField("Password");
        JPasswordField confirmPasswordField = new JPasswordField("Confirm Password");
        JTextField salaryField = new JTextField("Salary");

        JTextField[] fields = { nameField, emailField, phoneField, usernameField, passwordField, confirmPasswordField, salaryField };
        for (JTextField field : fields) {
            field.setMaximumSize(new Dimension(400, 40));
            field.setForeground(Color.GRAY);
            field.setBackground(Color.DARK_GRAY);
            field.setCaretColor(Color.WHITE);
            field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            field.setFont(new Font("SansSerif", Font.PLAIN, 15));
            formPanel.add(field);
            formPanel.add(Box.createVerticalStrut(20)); // ✅ spacing between fields
        }

        passwordField.setEchoChar((char) 0);
        confirmPasswordField.setEchoChar((char) 0);
        addPlaceholderBehavior(nameField, "Name");
        addPlaceholderBehavior(emailField, "Email");
        addPlaceholderBehavior(phoneField, "Phone");
        addPlaceholderBehavior(usernameField, "Username");
        addPlaceholderBehavior(passwordField, "Password", true);
        addPlaceholderBehavior(confirmPasswordField, "Confirm Password", true);
        addPlaceholderBehavior(salaryField, "Salary");

        // 📤 Create Button
        JButton createBtn = new JButton("Create Account");
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setBackground(new Color(0, 128, 0));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setMaximumSize(new Dimension(400, 45));

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createBtn);

        add(logo, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // 🎯 SIGNUP ACTION
        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                BigDecimal salary = new BigDecimal(salaryField.getText().trim());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Staff staff = new Staff(name, email, phone, username, salary);

                boolean success = authController.signup(staff, password);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed. Email might already be used.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    // ♻️ Placeholder handling
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
                        pf.setEchoChar('•');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (isPassword && field instanceof JPasswordField pf) {
                        pf.setEchoChar((char) 0);
                    }
                }
            }
        });
    }
}
