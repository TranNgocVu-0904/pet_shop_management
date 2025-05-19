package model;

import java.math.BigDecimal;

public class BillItem {
    public enum ItemType { PRODUCT, PET }

    private ItemType itemType;
    private Integer productId;
    private Integer petId;
    private String itemName;
    private BigDecimal unitPrice;
    private int quantity;

    // Constructor for product
    public BillItem(int productId, String name, BigDecimal unitPrice, int quantity) {
        this.itemType = ItemType.PRODUCT;
        this.productId = productId;
        this.itemName = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Constructor for pet
    public BillItem(int petId, String name, BigDecimal unitPrice) {
        this.itemType = ItemType.PET;
        this.petId = petId;
        this.itemName = name;
        this.unitPrice = unitPrice;
        this.quantity = 1;
    }

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public ItemType getItemType() { return itemType; }
    public Integer getProductId() { return productId; }
    public Integer getPetId() { return petId; }
    public String getItemName() { return itemName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (itemType == ItemType.PET)
            throw new UnsupportedOperationException("Pet quantity is fixed to 1");
        this.quantity = quantity;
    }
}
