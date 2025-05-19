package view;

import controller.PetController;
import model.Cat;
import model.Dog;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PetPanel extends JPanel {
    private JTable petTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> priceOrderBox;

    public PetPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // === Top Panel ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);

        // LEFT: [+] Button
        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
        addBtn.setBackground(new Color(0, 128, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(50, 40));
        topPanel.add(addBtn, BorderLayout.WEST);

        // RIGHT: Search + Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setBackground(Color.BLACK);

        searchField = new JTextField("Enter ID...", 10);
        styleTextField(searchField, "Enter ID...");

        JButton searchBtn = new JButton("🔍");
        styleButton(searchBtn);

        categoryBox = new JComboBox<>(new String[]{"All", "DOG", "CAT"});
        priceOrderBox = new JComboBox<>(new String[]{"None", "ASC", "DESC"});

        filterPanel.add(searchField);
        filterPanel.add(searchBtn);
        filterPanel.add(categoryBox);
        filterPanel.add(priceOrderBox);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // === Table Setup ===
        String[] columns = {"ID", "Name", "Type", "Breed", "Age", "Price", "✏️", "🗑️"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };

        petTable = new JTable(model);
        petTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(petTable);

        petTable.getColumn("✏️").setCellRenderer(new ButtonRenderer("✏️"));
        petTable.getColumn("🗑️").setCellRenderer(new ButtonRenderer("🗑️"));
        petTable.getColumn("✏️").setCellEditor(new ButtonEditor(new JCheckBox(), this, "update"));
        petTable.getColumn("🗑️").setCellEditor(new ButtonEditor(new JCheckBox(), this, "delete"));

        // === Action Listeners ===
        addBtn.addActionListener(e -> new PetDialog(this, null));

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                Pet pet = PetController.getAllPets().stream()
                        .filter(p -> p.getId() == id)
                        .findFirst().orElse(null);
                model.setRowCount(0);
                if (pet != null) {
                    addPetToTable(pet);
                } else {
                    JOptionPane.showMessageDialog(this, "No pet found with ID " + id);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID.");
            }
        });

        categoryBox.addActionListener(e -> applyFilters());
        priceOrderBox.addActionListener(e -> applyFilters());

        // === Load Initial Data ===
        loadAllPets();

        // === Add Components ===
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllPets() {
        model.setRowCount(0);
        List<Pet> pets = PetController.getAllPets();
        pets.forEach(this::addPetToTable);
    }

    private void applyFilters() {
        String type = categoryBox.getSelectedItem().toString();
        if (type.equals("All")) type = null;

        String priceOrder = priceOrderBox.getSelectedItem().toString();
        if (priceOrder.equals("None")) priceOrder = null;

        List<Pet> filtered = PetController.getPetsByFilter(type, priceOrder);
        model.setRowCount(0);
        filtered.forEach(this::addPetToTable);
    }

    private void addPetToTable(Pet p) {
        model.addRow(new Object[]{
            p.getId(),
            p.getName(),
            p.getClass().getSimpleName().toUpperCase(),
            p.getBreed(),
            p.getAge(),
            p.getPrice(),
            "✏️",
            "🗑️"
        });
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setBackground(Color.DARK_GRAY);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setPreferredSize(new Dimension(150, 35));
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public JTable getPetTable() {
        return petTable;
    }

    public void refreshTable() {
        loadAllPets();
    }
}
