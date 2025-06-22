package view.panel;

import controller.product.ProductController;
import controller.pet.PetController;
import controller.customer.CustomerController;
import controller.bill.BillingController;
import controller.user.AuthController;
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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class HomePanel extends JPanel {
    
    private JLabel timeLabel;
    private JLabel statusLabel;
    private Timer refreshTimer;
    private JFrame parentFrame;
    
    // Colors for modern theme
    private final Color primaryColor = new Color(0x2C3E50);
    private final Color successColor = new Color(0x27AE60);
    private final Color infoColor = new Color(0x3498DB);
    private final Color warningColor = new Color(0xF39C12);
    private final Color dangerColor = new Color(0xE74C3C);
    private final Color cardBg = Color.WHITE;
    private final Color lightBg = new Color(0xF8F9FA);
    
    private PetController petController = new PetController();
    private ProductController productController = new ProductController();

    public HomePanel() {
        initializePanel();
        setupDashboard();
        startAutoRefresh();
    }
    
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(lightBg);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void setupDashboard() {
        // Main container with scroll pane for better UX
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setOpaque(false);

        // Header section
        JPanel headerPanel = createHeaderPanel();
        
        // Statistics cards section
        JPanel statsPanel = createStatsPanel();
        
        // Charts section
        JPanel chartsPanel = createChartsPanel();
        
        // Quick actions section
        JPanel actionsPanel = createQuickActionsPanel();

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with scroll
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(chartsPanel, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setPreferredSize(new Dimension(0, 100));

        // Welcome section
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Analytics Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Welcome, " + AuthController.currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(new Color(0xBDC3C7));
        userLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(userLabel);

        // Time and status section
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setForeground(Color.WHITE);
        updateTime();

        statusLabel = new JLabel("System Online");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0x2ECC71));
        statusLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

        statusPanel.add(statusLabel);
        statusPanel.add(timeLabel);

        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);

        // Title
        JLabel statsTitle = new JLabel("Key Performance Indicators");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(primaryColor);
        statsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        statsContainer.add(statsTitle, BorderLayout.NORTH);

        // Stats grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        statsGrid.setOpaque(false);

        // Fetch data
        BillingController billing = new BillingController();
        UserDAO userDAO = new UserDAO();
        

 
        int totalProducts = productController.getAllProducts().size();
        int totalPets = petController.getAllPets().size();
        int totalCustomers = CustomerController.getAllCustomers().size();
        int totalOrders = billing.getTotalOrders();
        BigDecimal totalRevenue = billing.getTotalRevenue();
        
        int totalStaff = 0;
        try {
            totalStaff = userDAO.getAllStaff().size();
        } catch (Exception e) {
            totalStaff = 0;
        }
        
        // Calculate additional stats
        BigDecimal avgOrderValue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        int lowStockProducts = getLowStockCount();

        // Create enhanced stat cards with trend indicators
        statsGrid.add(createStatCard("Total Pets", String.valueOf(totalPets), successColor, "Active pets in system", "+12%"));
        statsGrid.add(createStatCard("Products", String.valueOf(totalProducts), infoColor, "Available products", "+5%"));
        statsGrid.add(createStatCard("Customers", String.valueOf(totalCustomers), warningColor, "Registered customers", "+8%"));
        statsGrid.add(createStatCard("Staff", String.valueOf(totalStaff), primaryColor, "Active staff members", "0%"));
        
        statsGrid.add(createStatCard("Orders", String.valueOf(totalOrders), new Color(0x9B59B6), "Orders processed", "+15%"));
        statsGrid.add(createStatCard("Revenue", "$" + totalRevenue, successColor, "Total sales revenue", "+22%"));
        statsGrid.add(createStatCard("Avg Order", "$" + avgOrderValue, infoColor, "Average order value", "+3%"));
        statsGrid.add(createStatCard("Low Stock", String.valueOf(lowStockProducts), dangerColor, "Products need restock", "-2"));

        statsContainer.add(statsGrid, BorderLayout.CENTER);
        return statsContainer;
    }

    private JPanel createChartsPanel() {
        JPanel chartsContainer = new JPanel(new BorderLayout());
        chartsContainer.setOpaque(false);
        chartsContainer.setPreferredSize(new Dimension(0, 600));

        // Title
        JLabel chartsTitle = new JLabel("Business Analytics");
        chartsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        chartsTitle.setForeground(primaryColor);
        chartsTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        chartsContainer.add(chartsTitle, BorderLayout.NORTH);

        // Charts grid
        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        chartsGrid.setOpaque(false);

        // Create charts
        chartsGrid.add(createPetDistributionChart());
        chartsGrid.add(createRevenueChart());
        chartsGrid.add(createCustomerGrowthChart());
        chartsGrid.add(createInventoryChart());

        chartsContainer.add(chartsGrid, BorderLayout.CENTER);
        return chartsContainer;
    }

    private JPanel createPetDistributionChart() {
        // Create pie chart for pet distribution
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Sample data - you can replace with actual data
        int totalPets = petController.getAllPets().size();
        dataset.setValue("Dogs", totalPets * 0.6);
        dataset.setValue("Cats", totalPets * 0.3);
        dataset.setValue("Others", totalPets * 0.1);

        JFreeChart chart = ChartFactory.createPieChart(
            "Pet Distribution", dataset, true, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Dogs", new Color(0x3498DB));
        plot.setSectionPaint("Cats", new Color(0xE74C3C));
        plot.setSectionPaint("Others", new Color(0xF39C12));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createRevenueChart() {
        // Create bar chart for monthly revenue
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sample data - last 6 months
        dataset.addValue(1500, "Revenue", "Jan");
        dataset.addValue(2300, "Revenue", "Feb");
        dataset.addValue(1800, "Revenue", "Mar");
        dataset.addValue(2700, "Revenue", "Apr");
        dataset.addValue(3200, "Revenue", "May");
        dataset.addValue(2900, "Revenue", "Jun");

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Revenue Trend", "Month", "Revenue ($)", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, successColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createCustomerGrowthChart() {
        // Create line chart for customer growth
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sample data
        dataset.addValue(50, "Customers", "Jan");
        dataset.addValue(65, "Customers", "Feb");
        dataset.addValue(78, "Customers", "Mar");
        dataset.addValue(92, "Customers", "Apr");
        dataset.addValue(108, "Customers", "May");
        dataset.addValue(125, "Customers", "Jun");

        JFreeChart chart = ChartFactory.createLineChart(
            "Customer Growth", "Month", "Total Customers", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, infoColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createInventoryChart() {
        // Create bar chart for inventory status
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sample data
        dataset.addValue(45, "Stock", "Food");
        dataset.addValue(23, "Stock", "Medicine");
        dataset.addValue(67, "Stock", "Toys");
        dataset.addValue(12, "Stock", "Accessories");

        JFreeChart chart = ChartFactory.createBarChart(
            "Inventory Status", "Product Type", "Stock Level", 
            dataset, PlotOrientation.VERTICAL, false, true, false);
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, warningColor);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 250));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel actionsContainer = new JPanel(new BorderLayout());
        actionsContainer.setOpaque(false);
        actionsContainer.setPreferredSize(new Dimension(0, 200));

        // Title
        JLabel actionsTitle = new JLabel("Quick Actions & Insights");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsTitle.setForeground(primaryColor);
        actionsTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        actionsContainer.add(actionsTitle, BorderLayout.NORTH);

        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsGrid.setOpaque(false);

        // Recent Activity Panel
        actionsGrid.add(createRecentActivityPanel());
        
        // Quick Actions Panel
        actionsGrid.add(createActionsButtonsPanel());
        
        // System Status Panel
        actionsGrid.add(createSystemStatusPanel());

        actionsContainer.add(actionsGrid, BorderLayout.CENTER);
        return actionsContainer;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);

        // Add recent activities
        activityList.add(createActivityItem("New customer registered", "2 min ago", successColor));
        activityList.add(createActivityItem("Product added to inventory", "5 min ago", infoColor));
        activityList.add(createActivityItem("Order completed", "8 min ago", warningColor));
        activityList.add(createActivityItem("Staff login", "12 min ago", primaryColor));

        panel.add(title, BorderLayout.NORTH);
        panel.add(activityList, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionsButtonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel actionsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        actionsGrid.setOpaque(false);

        actionsGrid.add(createQuickActionButton("New Sale", "Start new sale", successColor, "bill"));
        actionsGrid.add(createQuickActionButton("Add Pet", "Register new pet", infoColor, "pet"));
        actionsGrid.add(createQuickActionButton("Inventory", "Check stock", warningColor, "product"));
        actionsGrid.add(createQuickActionButton("Reports", "View reports", primaryColor, "reports"));

        panel.add(title, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSystemStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("System Status");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(primaryColor);

        JPanel statusList = new JPanel();
        statusList.setLayout(new BoxLayout(statusList, BoxLayout.Y_AXIS));
        statusList.setOpaque(false);

        statusList.add(createStatusItem("Database", "Connected", successColor));
        statusList.add(createStatusItem("Backup", "Last: 2 hours ago", infoColor));
        statusList.add(createStatusItem("Users Online", "3 active", warningColor));
        statusList.add(createStatusItem("Memory Usage", "68% used", dangerColor));

        panel.add(title, BorderLayout.NORTH);
        panel.add(statusList, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String description, String trend) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBg);
        card.setPreferredSize(new Dimension(200, 140));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.brighter(), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color.brighter().brighter());
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardBg);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Top section with title and trend
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(primaryColor);

        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        trendLabel.setForeground(trend.startsWith("+") ? successColor : 
                                trend.startsWith("-") ? dangerColor : primaryColor);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(trendLabel, BorderLayout.EAST);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(new Color(0x7F8C8D));

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActivityItem(String activity, String time, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(4, 20));

        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        activityLabel.setForeground(primaryColor);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        timeLabel.setForeground(new Color(0x7F8C8D));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(activityLabel, BorderLayout.NORTH);
        textPanel.add(timeLabel, BorderLayout.SOUTH);

        item.add(colorIndicator, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JPanel createStatusItem(String label, String status, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(primaryColor);

        JLabel statusText = new JLabel(status);
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusText.setForeground(color);

        item.add(labelText, BorderLayout.WEST);
        item.add(statusText, BorderLayout.EAST);

        return item;
    }

    private JButton createQuickActionButton(String text, String tooltip, Color color, String panelKey) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to navigate to panel
        button.addActionListener(e -> {
            if (parentFrame != null && parentFrame instanceof view.frame.HomeFrame) {
                view.frame.HomeFrame homeFrame = (view.frame.HomeFrame) parentFrame;
                navigateToPanel(panelKey);
                
                // Show notification
                JOptionPane.showMessageDialog(
                    parentFrame,
                    "Navigating to " + text + " section...",
                    "Quick Action",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }
    
    private void navigateToPanel(String panelKey) {
        // This will trigger panel navigation
        SwingUtilities.invokeLater(() -> {
            Container parent = this.getParent();
            while (parent != null && !(parent instanceof view.frame.HomeFrame)) {
                parent = parent.getParent();
            }
            
            if (parent instanceof view.frame.HomeFrame) {
                // Use reflection to access the CardLayout and show the panel
                try {
                    java.lang.reflect.Field layoutField = parent.getClass().getDeclaredField("layout");
                    java.lang.reflect.Field contentPanelField = parent.getClass().getDeclaredField("contentPanel");
                    
                    layoutField.setAccessible(true);
                    contentPanelField.setAccessible(true);
                    
                    CardLayout layout = (CardLayout) layoutField.get(parent);
                    JPanel contentPanel = (JPanel) contentPanelField.get(parent);
                    
                    layout.show(contentPanel, panelKey);
                } catch (Exception ex) {
                    // Fallback: just show message
                    System.out.println("Navigating to: " + panelKey);
                }
            }
        });
    }

    private int getLowStockCount() {
        // Placeholder - implement actual low stock logic
        return (int) (Math.random() * 5) + 1;
    }

    private void updateTime() {
        if (timeLabel != null) {
            String currentTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );
            timeLabel.setText(currentTime);
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateTime());
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}
