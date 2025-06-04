package controller;

import dao.BillDAO;
import dao.CustomerDAO;
import dao.PetDAO;
import dao.ProductDAO;
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
    private final PetDAO petDao = new PetDAO();
    private final ProductDAO productDao = new ProductDAO(); 

    public void addProductToCart(Product product, int quantity) {
        BillItem existingItem = cart.getItem(product.getId());
        int currentInCart = existingItem != null ? existingItem.getQuantity() : 0;
        int remainingStock = product.getStockQuantity() - currentInCart;

        if (quantity > remainingStock) {
            throw new IllegalArgumentException("Only " + remainingStock + " items available for " + product.getName());
        }

        cart.addItem(product, quantity);
    }
    
    public void addPetToCart(Pet pet) {
        cart.addItem(pet);
    }

    public void updateCartItem(int productId, int newQuantity) {
        Product product = ProductController.getAllProducts().stream()
            .filter(p -> p.getId() == productId)
            .findFirst()
            .orElse(null);

        if (product == null) return;

        if (newQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Stock exceeded. Only " + product.getStockQuantity() + " available.");
        }

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

        // Validate availability
        for (BillItem item : items) {
            if (item.getItemType() == BillItem.ItemType.PRODUCT) {
                Product product = productDao.getById(item.getProductId());
                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for " + product.getName() +
                            ". Required: " + item.getQuantity() + ", Available: " + product.getStockQuantity());
                }
            }
        }

        boolean success = billingService.processPayment(bill, items);
        if (!success) return false;

        for (BillItem item : items) {
            if (item.getItemType() == BillItem.ItemType.PET) {
                petDao.markPetSold(item.getPetId());
            }
        }

        return true;
    }

    public void applyLoyaltyPoints(Customer customer, BigDecimal total) throws SQLException {
        int points = total.divide(BigDecimal.valueOf(10), RoundingMode.DOWN).intValue();
        if (points > 0) {
            customer.addLoyaltyPoints(points);
            customerDao.updateCustomer(customer);
        }
    }

    public void exportBillAsPdf(Bill bill) {
        PdfGenerator.generateBillPdf(bill);
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
