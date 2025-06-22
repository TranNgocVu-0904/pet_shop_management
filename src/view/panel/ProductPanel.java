package view.panel;

import controller.product.ProductController;
import model.product.Food;
import model.product.Medicine;
import model.product.Product;
import model.product.Toy;
import util.ui.ButtonCellEditor;
import util.ui.ButtonCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import view.dialog.ProductFormDialog;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> priceOrderBox;

    // Danh sách sản phẩm hiện tại được load từ DB
    private List<Product> currentProducts;

    // Controller
    private final ProductController productController = new ProductController();

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

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

        categoryBox = new JComboBox<>(new String[]{"All", "TOY", "FOOD", "MEDICINE"});
        priceOrderBox = new JComboBox<>(new String[]{"None", "ASC", "DESC"});
        styleComboBox(categoryBox);
        styleComboBox(priceOrderBox);

        filterPanel.add(searchField);
        filterPanel.add(searchBtn);
        filterPanel.add(categoryBox);
        filterPanel.add(priceOrderBox);
        topPanel.add(filterPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Name", "Type", "Quantity", "Price", "Details", "Update", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7; // chỉ cột Update và Delete được edit (bấm button)
            }
        };

        productTable = new JTable(model);
        productTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Renderer và Editor cho cột Update (✏️) và Delete (🗑️)
        productTable.getColumn("Update").setCellRenderer(new ButtonCellRenderer("✏️"));
        productTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        productTable.getColumn("Update").setCellEditor(new ButtonCellEditor<>(
                productTable,
                "update",
                this::mapRowToProduct,
                product -> {
                    // Mở dialog sửa sản phẩm, khi dialog đóng gọi refreshTable để cập nhật lại bảng
                    ProductFormDialog dialog = new ProductFormDialog(this, product);
                    dialog.setVisible(true);
                    dialog.setVisible(true);
                    refreshTable();
                },
                null
        ));

        productTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
                productTable,
                "delete",
                this::mapRowToProduct,
                null,
                product -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Delete product ID " + product.getId() + "?", "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = productController.deleteProduct(product.getId());
                        if(success) {
                            JOptionPane.showMessageDialog(this, "Deleted product ID " + product.getId());
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete product ID " + product.getId());
                        }
                    }
                }
        ));

        addBtn.addActionListener(e -> {
            // Mở dialog thêm mới sản phẩm, khi dialog đóng gọi refreshTable
            ProductFormDialog dialog = new ProductFormDialog(this, null);
            dialog.setVisible(true);
            refreshTable();
        });

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                Product p = currentProducts.stream()
                        .filter(prod -> prod.getId() == id)
                        .findFirst().orElse(null);
                model.setRowCount(0);
                if (p != null) addProductToTable(p);
                else JOptionPane.showMessageDialog(this, "No product found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });

        categoryBox.addActionListener(e -> applyFilters());
        priceOrderBox.addActionListener(e -> applyFilters());

        loadAllProducts();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllProducts() {
        model.setRowCount(0);
        currentProducts = productController.getAllProducts();
        currentProducts.forEach(this::addProductToTable);
    }

    private void applyFilters() {
        String category = categoryBox.getSelectedItem().toString();
        if (category.equals("All")) category = null;

        String priceOrder = priceOrderBox.getSelectedItem().toString();
        if (priceOrder.equals("None")) priceOrder = null;

        currentProducts = productController.getProductsByFilter(category, priceOrder);
        model.setRowCount(0);
        currentProducts.forEach(this::addProductToTable);
    }

    private void addProductToTable(Product p) {
        String details = "-";
        if (p instanceof Toy t) {
            details = "Material: " + t.getMaterial();
        } else if (p instanceof Food f) {
            details = "Exp: " + f.getExpirationDate() + ", Info: " + f.getNutritionalInfo();
        } else if (p instanceof Medicine m) {
            details = "Dose: " + m.getDosage() + ", Exp: " + m.getExpirationDate();
        }

        model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getClass().getSimpleName().toUpperCase(),
                p.getStockQuantity(),
                p.getPrice(),
                details,
                "✏️",
                "🗑️"
        });
    }

    private Product mapRowToProduct(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        return currentProducts.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID " + id));
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

    public JTable getProductTable() {
        return productTable;
    }

    public void refreshTable() {
        loadAllProducts();
    }
}
