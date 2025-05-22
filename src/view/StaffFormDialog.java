package view;

import controller.AuthController;
import controller.StaffController;
import model.Manager;
import model.Staff;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import util.BCrypt;

public class StaffFormDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField salaryField;

    private final StaffPanel parent;
    private final Staff existingStaff;

    private final boolean isManager = AuthController.currentUser instanceof Manager;

    public StaffFormDialog(StaffPanel parent, Staff staff) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        this.parent = parent;
        this.existingStaff = staff;

        setTitle(staff == null ? "Add Staff" : "Update Staff");
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Always show these
        form.add(new JLabel("Name:"));   form.add(nameField);
        form.add(new JLabel("Email:"));  form.add(emailField);
        form.add(new JLabel("Phone:"));  form.add(phoneField);

        if (isManager) {
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);
            salaryField = new JTextField(20);

            form.add(new JLabel("Username:")); form.add(usernameField);
            form.add(new JLabel("Password:")); form.add(passwordField);
            form.add(new JLabel("Salary:"));   form.add(salaryField);
        }

        if (staff != null) {
            nameField.setText(staff.getName());
            emailField.setText(staff.getEmail());
            phoneField.setText(staff.getPhone());

            if (isManager) {
                usernameField.setText(staff.getUsername());
                passwordField.setText("********"); // password hidden
                salaryField.setText(staff.getSalary().toPlainString());
            }
        }

        JButton saveBtn = new JButton(staff == null ? "Create" : "Update");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> saveStaff());
        cancelBtn.addActionListener(e -> dispose());

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void saveStaff() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            StaffController controller = new StaffController();

            if (existingStaff != null) {
                existingStaff.setName(name);
                existingStaff.setEmail(email);
                existingStaff.setPhone(phone);

                if (isManager) {
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();
                    BigDecimal salary = new BigDecimal(salaryField.getText().trim());

                    existingStaff.setUsername(username);
                    if (!password.equals("********")) {
                        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                        existingStaff.setPasswordHash(hashed);
                    }
                }

                if (!controller.updateStaff(existingStaff)) {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            } else {
                if (!isManager) {
                    JOptionPane.showMessageDialog(this, "Only managers can add staff.");
                    return;
                }

                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                BigDecimal salary = new BigDecimal(salaryField.getText().trim());

                if (username.isBlank() || password.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Username and password are required.");
                    return;
                }

                Staff newStaff = new Staff(name, email, phone, username, password, salary);
                if (!controller.addStaff(newStaff)) {
                    JOptionPane.showMessageDialog(this, "Add failed.");
                }
            }

            parent.refreshTable();
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
