package view.panel;

import controller.product.ProductController;
import controller.pet.PetController;
import controller.customer.CustomerController;
import controller.bill.BillingController;
import dao.user.UserDAO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

public class ReportsPanel extends JPanel {
    
    // Colors for consistent theme
    private final Color primaryColor = new Color(0x2C3E50);
    private final Color successColor = new Color(0x27AE60);
    private final Color infoColor = new Color(0x3498DB);
    private final Color warningColor = new Color(0xF39C12);
    private final Color dangerColor = new Color(0xE74C3C);
    private final Color cardBg = Color.WHITE;
    private final Color lightBg = new Color(0xF8F9FA);

    private JTabbedPane tabbedPane;
    private BillingController billingController;
    private UserDAO userDAO;
    
    private PetController petController = new PetController();
    private ProductController productController = new ProductController();

    public ReportsPanel() {
        billingController = new BillingController();
        userDAO = new UserDAO();
        
        initializePanel();
        setupReportTabs();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(lightBg);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setupReportTabs() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Tabbed pane for different reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(cardBg);
        
        // Add report tabs
        tabbedPane.addTab("📊 Sales Report", createSalesReportPanel());
        tabbedPane.addTab("📈 Financial Report", createFinancialReportPanel());
        tabbedPane.addTab("🐾 Pet Statistics", createPetReportPanel());
        tabbedPane.addTab("📦 Inventory Report", createInventoryReportPanel());
        tabbedPane.addTab("👥 Customer Report", createCustomerReportPanel());
        tabbedPane.addTab("👨‍💼 Staff Report", createStaffReportPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("Business Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Comprehensive reporting dashboard for business insights");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(0xBDC3C7));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Export buttons
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setOpaque(false);
        
        JButton exportBtn = createStyledButton("Export Data", "Export current data to CSV");
        exportBtn.addActionListener(e -> exportCurrentData());
        
        JButton refreshBtn = createStyledButton("Refresh", "Refresh all reports");
        refreshBtn.addActionListener(e -> refreshAllReports());

        exportPanel.add(refreshBtn);
        exportPanel.add(exportBtn);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(exportPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Summary cards
        JPanel summaryPanel = createSalesSummaryCards();
        
        // Charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.add(createMonthlySalesChart());
        chartsPanel.add(createTopProductsChart());

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSalesSummaryCards() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        int totalOrders = billingController.getTotalOrders();
        BigDecimal totalRevenue = billingController.getTotalRevenue();
        BigDecimal avgOrderValue = totalOrders > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        summaryPanel.add(createSummaryCard("Total Orders", String.valueOf(totalOrders), successColor));
        summaryPanel.add(createSummaryCard("Total Revenue", "$" + totalRevenue, infoColor));
        summaryPanel.add(createSummaryCard("Average Order", "$" + avgOrderValue, warningColor));
        summaryPanel.add(createSummaryCard("Best Month", "June 2024", primaryColor));

        return summaryPanel;
    }

    private JPanel createFinancialReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Financial metrics
        JPanel metricsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(0, 250));

        BigDecimal totalRevenue = billingController.getTotalRevenue();
        BigDecimal estimatedCosts = totalRevenue.multiply(BigDecimal.valueOf(0.6)); // 60% costs
        BigDecimal estimatedProfit = totalRevenue.subtract(estimatedCosts);

        metricsPanel.add(createSummaryCard("Total Revenue", "$" + totalRevenue, successColor));
        metricsPanel.add(createSummaryCard("Estimated Costs", "$" + estimatedCosts, dangerColor));
        metricsPanel.add(createSummaryCard("Estimated Profit", "$" + estimatedProfit, infoColor));
        metricsPanel.add(createSummaryCard("Profit Margin", "40%", warningColor));
        metricsPanel.add(createSummaryCard("Growth Rate", "+15%", successColor));
        metricsPanel.add(createSummaryCard("ROI", "25%", primaryColor));

        // Revenue trend chart
        JPanel chartPanel = createRevenueFlowChart();

        panel.add(metricsPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPetReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Pet statistics
        int totalPets = petController.getAllPets().size();
        int soldPets = (int)(totalPets * 0.3); // Sample data
        int availablePets = totalPets - soldPets;

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        statsPanel.add(createSummaryCard("Total Pets", String.valueOf(totalPets), infoColor));
        statsPanel.add(createSummaryCard("Sold Pets", String.valueOf(soldPets), successColor));
        statsPanel.add(createSummaryCard("Available Pets", String.valueOf(availablePets), warningColor));

        // Pet distribution charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.add(createPetTypeChart());
        chartsPanel.add(createPetAgeChart());

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inventory summary
        int totalProducts = productController.getAllProducts().size();
        int lowStockItems = (int)(totalProducts * 0.2); // Sample data
        int outOfStockItems = (int)(totalProducts * 0.05);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Products", String.valueOf(totalProducts), infoColor));
        summaryPanel.add(createSummaryCard("Low Stock", String.valueOf(lowStockItems), warningColor));
        summaryPanel.add(createSummaryCard("Out of Stock", String.valueOf(outOfStockItems), dangerColor));
        summaryPanel.add(createSummaryCard("Well Stocked", String.valueOf(totalProducts - lowStockItems - outOfStockItems), successColor));

        // Inventory table
        JPanel tablePanel = createInventoryTable();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        int totalCustomers = CustomerController.getAllCustomers().size();
        int activeCustomers = (int)(totalCustomers * 0.8);
        int newCustomers = (int)(totalCustomers * 0.2);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Customers", String.valueOf(totalCustomers), infoColor));
        summaryPanel.add(createSummaryCard("Active Customers", String.valueOf(activeCustomers), successColor));
        summaryPanel.add(createSummaryCard("New This Month", String.valueOf(newCustomers), warningColor));

        // Customer activity chart
        JPanel chartPanel = createCustomerActivityChart();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStaffReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightBg);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        int totalStaff = 0;
        try {
            totalStaff = userDAO.getAllStaff().size();
        } catch (Exception e) {
            totalStaff = 0;
        }

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 120));

        summaryPanel.add(createSummaryCard("Total Staff", String.valueOf(totalStaff), primaryColor));
        summaryPanel.add(createSummaryCard("Active Today", String.valueOf(Math.max(1, totalStaff - 1)), successColor));
        summaryPanel.add(createSummaryCard("Performance", "92%", infoColor));

        // Staff performance chart
        JPanel chartPanel = createStaffPerformanceChart();

        panel.add(summaryPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    // Helper method to create summary cards
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter(), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(primaryColor);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Chart creation methods
    private JPanel createMonthlySalesChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1200, "Sales", "Jan");
        dataset.addValue(1800, "Sales", "Feb");
        dataset.addValue(1500, "Sales", "Mar");
        dataset.addValue(2200, "Sales", "Apr");
        dataset.addValue(2800, "Sales", "May");
        dataset.addValue(3200, "Sales", "Jun");

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Sales Trend", "Month", "Sales ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Sales Performance"));
        return chartPanel;
    }

    private JPanel createTopProductsChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Pet Food", 35);
        dataset.setValue("Toys", 25);
        dataset.setValue("Medicine", 20);
        dataset.setValue("Accessories", 20);

        JFreeChart chart = ChartFactory.createPieChart(
            "Top Products by Sales", dataset, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Pet Food", successColor);
        plot.setSectionPaint("Toys", infoColor);
        plot.setSectionPaint("Medicine", warningColor);
        plot.setSectionPaint("Accessories", primaryColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Product Distribution"));
        return chartPanel;
    }

    private JPanel createRevenueFlowChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1500, "Revenue", "Week 1");
        dataset.addValue(2300, "Revenue", "Week 2");
        dataset.addValue(1800, "Revenue", "Week 3");
        dataset.addValue(2700, "Revenue", "Week 4");

        JFreeChart chart = ChartFactory.createLineChart(
            "Weekly Revenue Flow", "Week", "Revenue ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, infoColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Revenue Trend"));
        return chartPanel;
    }

    private JPanel createPetTypeChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Dogs", 60);
        dataset.setValue("Cats", 30);
        dataset.setValue("Birds", 10);

        JFreeChart chart = ChartFactory.createPieChart(
            "Pet Type Distribution", dataset, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Dogs", infoColor);
        plot.setSectionPaint("Cats", dangerColor);
        plot.setSectionPaint("Birds", warningColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Pet Types"));
        return chartPanel;
    }

    private JPanel createPetAgeChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, "Count", "Puppy/Kitten");
        dataset.addValue(25, "Count", "Young");
        dataset.addValue(20, "Count", "Adult");
        dataset.addValue(8, "Count", "Senior");

        JFreeChart chart = ChartFactory.createBarChart(
            "Pet Age Distribution", "Age Group", "Count", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, warningColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Age Groups"));
        return chartPanel;
    }

    private JPanel createInventoryTable() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        
        String[] columns = {"Product", "Type", "Stock", "Status", "Reorder Level"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Sample data
        model.addRow(new Object[]{"Dog Food Premium", "Food", "45", "Good", "10"});
        model.addRow(new Object[]{"Cat Medicine", "Medicine", "8", "Low", "10"});
        model.addRow(new Object[]{"Rubber Ball", "Toy", "0", "Out", "5"});
        model.addRow(new Object[]{"Pet Collar", "Accessory", "23", "Good", "5"});

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Status"));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createCustomerActivityChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(45, "Customers", "Jan");
        dataset.addValue(52, "Customers", "Feb");
        dataset.addValue(48, "Customers", "Mar");
        dataset.addValue(65, "Customers", "Apr");
        dataset.addValue(70, "Customers", "May");
        dataset.addValue(78, "Customers", "Jun");

        JFreeChart chart = ChartFactory.createLineChart(
            "Customer Activity Growth", "Month", "Active Customers", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Customer Growth"));
        return chartPanel;
    }

    private JPanel createStaffPerformanceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(95, "Performance", "Sales");
        dataset.addValue(88, "Performance", "Customer Service");
        dataset.addValue(92, "Performance", "Efficiency");
        dataset.addValue(85, "Performance", "Punctuality");

        JFreeChart chart = ChartFactory.createBarChart(
            "Staff Performance Metrics", "Metric", "Score (%)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, primaryColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Performance Overview"));
        return chartPanel;
    }

    private JButton createStyledButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(infoColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        return btn;
    }

    private void exportCurrentData() {
        int selectedTab = tabbedPane.getSelectedIndex();
        String tabName = tabbedPane.getTitleAt(selectedTab);
        
        try {
            String fileName = "report_" + System.currentTimeMillis() + ".csv";
            FileWriter writer = new FileWriter(fileName);
            
            writer.write("Pet Shop Report - " + tabName + "\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            // Add sample data based on tab
            switch (selectedTab) {
                case 0: // Sales Report
                    writer.write("Metric,Value\n");
                    writer.write("Total Orders," + billingController.getTotalOrders() + "\n");
                    writer.write("Total Revenue,$" + billingController.getTotalRevenue() + "\n");
                    break;
                case 1: // Financial Report
                    writer.write("Financial Metric,Amount\n");
                    writer.write("Total Revenue,$" + billingController.getTotalRevenue() + "\n");
                    break;
                default:
                    writer.write("Data exported for " + tabName + "\n");
            }
            
            writer.close();
            JOptionPane.showMessageDialog(this, "Data exported to " + fileName, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllReports() {
        // Refresh all report data
        SwingUtilities.invokeLater(() -> {
            // Remove and re-add all tabs to refresh content
            tabbedPane.removeAll();
            tabbedPane.addTab("📊 Sales Report", createSalesReportPanel());
            tabbedPane.addTab("📈 Financial Report", createFinancialReportPanel());
            tabbedPane.addTab("🐾 Pet Statistics", createPetReportPanel());
            tabbedPane.addTab("📦 Inventory Report", createInventoryReportPanel());
            tabbedPane.addTab("👥 Customer Report", createCustomerReportPanel());
            tabbedPane.addTab("👨‍💼 Staff Report", createStaffReportPanel());
            
            JOptionPane.showMessageDialog(this, "All reports refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}