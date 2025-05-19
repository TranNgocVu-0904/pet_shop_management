package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int id;
    private int customerId;
    private int staffId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime transactionTime;
    private final List<BillItem> items = new ArrayList<>();

    public Bill(int customerId, int staffId, String paymentMethod) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.paymentMethod = paymentMethod;
        this.transactionTime = LocalDateTime.now();
    }

    public void addItem(BillItem item) {
        items.add(item);
        totalAmount = totalAmount == null ? item.getTotal() : totalAmount.add(item.getTotal());
    }

    public List<BillItem> getItems() {
        return new ArrayList<>(items);
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public int getStaffId() { return staffId; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
}
