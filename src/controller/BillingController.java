package controller;

import dao.BillDAO;
import dao.CustomerDAO;
import model.Customer;
import model.ShoppingCart;
import model.Product;
import model.Bill;
import model.BillItem;
import service.PdfGenerator;
import service.BillingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Pet;

public class BillingController {
    private final ShoppingCart cart = new ShoppingCart();
    private final BillingService billingService = new BillingService();
    private final CustomerDAO customerDao = new CustomerDAO();
    private final BillDAO billDao = new BillDAO();

    public void addProductToCart(Product product, int quantity) {
        cart.addItem(product, quantity);
    }

    public void addPetToCart(Pet pet) {
        cart.addItem(pet);
    }

    public void updateCartItem(int productId, int newQuantity) {
        cart.updateQuantity(productId, newQuantity);
    }

    public void removeCartItem(int productId) {
        cart.removeItem(productId);
    }

    public BigDecimal getCartTotal() {
        return cart.getTotal();
    }

    public List<BillItem> getCartItemsAsList() {
        return new ArrayList<>(cart.getItems().values());
    }

    public Bill finalizeBill(int customerId, int staffId, String paymentMethod) {
        Bill bill = new Bill(customerId, staffId, paymentMethod);
        getCartItemsAsList().forEach(bill::addItem);
        return bill;
    }

    public boolean processBill(Bill bill) throws SQLException {
        List<BillItem> items = bill.getItems();
        return billingService.processPayment(bill, items);
    }

    public void applyLoyaltyPoints(Customer customer, BigDecimal total) throws SQLException {
        int points = total.divide(BigDecimal.valueOf(10), RoundingMode.DOWN).intValue();
        if (points > 0) {
            customer.addLoyaltyPoints(points);
            customerDao.updateCustomer(customer);
        }
    }

    public void exportBillAsPdf(Bill bill, String filePath) {
        PdfGenerator.generateBillPdf(bill, filePath);
        cart.clear();
    }

    public void clearCart() {
        cart.clear();
    }
    
    // BillingController.java
    public BigDecimal getTotalRevenue() {
        return billDao.getTotalRevenue();
    }

    public int getTotalOrders() {
        return billDao.getTotalOrderCount();
    }
}
