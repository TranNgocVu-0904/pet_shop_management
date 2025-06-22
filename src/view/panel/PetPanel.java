package view.panel;

import controller.pet.PetController;
import model.pet.Cat;
import model.pet.Dog;
import model.pet.Pet;
import util.ui.ButtonCellEditor;
import util.ui.ButtonCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import view.dialog.PetFormDialog;

public class PetPanel extends JPanel {
    private JTable petTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> priceOrderBox;

    private final PetController petController;  // thêm controller instance

    public PetPanel() {
        petController = new PetController();  // tạo instance controller

        setLayout(new BorderLayout());
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // === Top Bar ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 236, 236));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton addBtn = createRoundedButton("+");
        addBtn.setPreferredSize(new Dimension(50, 40));
        topPanel.add(addBtn, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setBackground(new Color(240, 236, 236));

        searchField = createTextField("Enter ID...", 200, 35);
        JButton searchBtn = createRoundedButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 35));

        categoryBox = new JComboBox<>(new String[]{"All", "DOG", "CAT"});
        priceOrderBox = new JComboBox<>(new String[]{"None", "ASC", "DESC"});
        styleComboBox(categoryBox);
        styleComboBox(priceOrderBox);

        filterPanel.add(searchField);
        filterPanel.add(searchBtn);
        filterPanel.add(categoryBox);
        filterPanel.add(priceOrderBox);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // === Table Setup ===
        String[] columns = {"ID", "Name", "Type", "Breed", "Age", "Price", "Update", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };

        petTable = new JTable(model);
        petTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        petTable.getColumn("Update").setCellRenderer(new ButtonCellRenderer("✏️"));
        petTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        petTable.getColumn("Update").setCellEditor(new ButtonCellEditor<>(
                petTable,
                "update",
                this::mapRowToPet,
                pet -> new PetFormDialog(this, pet),
                null
        ));

        petTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
                petTable,
                "delete",
                this::mapRowToPet,
                null,
                pet -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn xóa thú cưng ID " + pet.getId() + "?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = petController.deletePet(pet.getId());  // gọi controller
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Xóa thú cưng thành công.");
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Không thể xóa thú cưng (có thể do ràng buộc khóa ngoại).");
                        }
                    }
                }
        ));


        addBtn.addActionListener(e -> new PetFormDialog(this, null));
        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                Pet pet = petController.getAllPets().stream()  // gọi qua instance
                        .filter(p -> p.getId() == id)
                        .findFirst().orElse(null);
                model.setRowCount(0);
                if (pet != null) addPetToTable(pet);
                else JOptionPane.showMessageDialog(this, "No pet found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });

        categoryBox.addActionListener(e -> applyFilters());
        priceOrderBox.addActionListener(e -> applyFilters());

        loadAllPets();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllPets() {
        model.setRowCount(0);
        List<Pet> pets = petController.getAllPets();  // gọi qua instance
        pets.forEach(this::addPetToTable);

        petTable.clearSelection();  // ✅ Bỏ chọn dòng đầu tiên
    }

    private void applyFilters() {
        String type = categoryBox.getSelectedItem().toString();
        if (type.equals("All")) type = null;

        String priceOrder = priceOrderBox.getSelectedItem().toString();
        if (priceOrder.equals("None")) priceOrder = null;

        List<Pet> filtered = petController.getPetsByFilter(type, priceOrder);  // gọi qua instance
        model.setRowCount(0);
        filtered.forEach(this::addPetToTable);

        petTable.clearSelection();  // ✅ Bỏ chọn dòng đầu tiên
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

    private Pet mapRowToPet(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = model.getValueAt(row, 1).toString();
        String type = model.getValueAt(row, 2).toString();
        String breed = model.getValueAt(row, 3).toString();
        int age = Integer.parseInt(model.getValueAt(row, 4).toString());
        BigDecimal price = new BigDecimal(model.getValueAt(row, 5).toString());

        Pet pet = switch (type) {
            case "DOG" -> new Dog(name, breed, age, price);
            case "CAT" -> new Cat(name, breed, age, price);
            default -> throw new IllegalArgumentException("Invalid pet type");
        };
        pet.setId(id);
        return pet;
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x007BFF));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(0x0056B3), 1, true));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x0056B3));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x007BFF));
            }
        });
        return button;
    }

    private JTextField createTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(width, height));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setPreferredSize(new Dimension(120, 35));
        box.setBackground(Color.WHITE);
        box.setForeground(Color.BLACK);
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public JTable getPetTable() {
        return petTable;
    }

    public void refreshTable() {
        loadAllPets();
    }
}
