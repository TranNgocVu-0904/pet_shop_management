package model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<Integer, BillItem> items = new HashMap<>();

    public void addItem(Product product, int quantity) {
        items.compute(product.getId(), (k, v) -> {
            if (v == null) {
                return new BillItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
                );
            }
            v.setQuantity(v.getQuantity() + quantity);
            return v;
        });
    }

    public void updateQuantity(int productId, int newQuantity) {
        BillItem item = items.get(productId);
        if (item != null) {
            item.setQuantity(newQuantity);
        }
    }

    public void removeItem(int productId) {
        items.remove(productId);
    }

    public BigDecimal getTotal() {
        return items.values().stream()
            .map(BillItem::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Integer, BillItem> getItems() {
        return new HashMap<>(items);
    }

    public void clear() {
        items.clear();
    }
}