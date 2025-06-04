package view;

import controller.AuthController;
import model.Staff;
import util.PasswordFieldUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;

public class SignupFrame extends JFrame {
    private final AuthController authController = new AuthController();

    public SignupFrame() {
        setTitle("Petshop - Sign Up");
        setSize(500, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

//        JLabel logo = new JLabel();
//        logo.setIcon(new ImageIcon(new ImageIcon("src/images/petshop_logo.png").getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH)));
//        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 236, 236));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JLabel title = new JLabel("Sign up");
        title.setForeground(new Color(125,194,255));
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JTextField nameField = createTextField("Name");
        JTextField emailField = createTextField("Email");
        JTextField phoneField = createTextField("Phone");
        JTextField usernameField = createTextField("Username");

        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JPanel passwordPanel = PasswordFieldUtil.createPasswordFieldWithToggle(passwordField, "Password");
        JPanel confirmPanel = PasswordFieldUtil.createPasswordFieldWithToggle(confirmPasswordField, "Confirm Password");

        JButton createBtn = new JButton("Create Account");
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setBackground(new Color(125, 235, 255));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setMaximumSize(new Dimension(300, 45));

        formPanel.add(title);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(phoneField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(confirmPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createBtn);

//        add(logo, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Staff staff = new Staff(name, email, phone, username, new BigDecimal(0));

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

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setMaximumSize(new Dimension(300, 40));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.LIGHT_GRAY);
        field.setCaretColor(Color.GRAY);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }
}

