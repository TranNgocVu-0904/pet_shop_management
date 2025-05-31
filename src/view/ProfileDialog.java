package view;

import controller.AuthController;
import controller.StaffController;
import model.Manager;
import model.Staff;
import model.SysUser;
import util.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProfileDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public ProfileDialog(Window owner) {
        super(owner, "My Profile", ModalityType.APPLICATION_MODAL);
        setSize(420, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> handleUpdate());

        add(formPanel, BorderLayout.CENTER);
        add(updateBtn, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private void loadData() {
        SysUser user = AuthController.currentUser;

        if (user == null) {
            JOptionPane.showMessageDialog(this, "No user session found.");
            dispose();
            return;
        }

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        usernameField.setText(user.getUsername());
    }

    private void handleUpdate() {
        SysUser user = AuthController.currentUser;

        if (user == null) return;

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (except password) are required");
            return;
        }

        // Update object
        SysUser updatedUser;
        if (user instanceof Staff staff) {
            updatedUser = new Staff(name, email, phone, username, staff.getPasswordHash(), staff.getSalary());
        } else if (user instanceof Manager manager) {
            updatedUser = new Manager(name, email, phone, username, manager.getPasswordHash());
        } else {
            JOptionPane.showMessageDialog(this, "Unsupported user type");
            return;
        }

        updatedUser.setId(user.getId());

        // Update password if provided
        if (!newPassword.isBlank()) {
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            updatedUser.setPasswordHash(hashed);
        }

        StaffController controller = new StaffController();
        boolean success = controller.updateUser(updatedUser); // safe cast even for Manager

        if (success) {
            AuthController.currentUser = updatedUser;
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.");
        }
    }
}
