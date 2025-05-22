package model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<Integer, BillItem> items = new HashMap<>();

    public void addItem(Product product, int quantity) {
        int key = product.getId(); // unique positive key for products
        items.compute(key, (k, v) -> {
            if (v == null) {
                return new BillItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity,
                    product.getClass().getSimpleName().toUpperCase()
                );
            }
            v.setQuantity(v.getQuantity() + quantity);
            return v;
        });
    }

    public void addItem(Pet pet) {
        int key = pet.getId() * -1; // negative ID key for pets
        if (!items.containsKey(key)) {
            items.put(key, new BillItem(
                pet.getId(),
                pet.getName(),
                pet.getPrice(),
                pet.getClass().getSimpleName().toUpperCase()
            ));
        }
    }

    public void updateQuantity(int productId, int newQuantity) {
        BillItem item = items.get(productId);
        if (item != null && item.getItemType() == BillItem.ItemType.PRODUCT) {
            item.setQuantity(newQuantity);
        }
    }

    public void removeItem(int id) {
        items.remove(id);
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
