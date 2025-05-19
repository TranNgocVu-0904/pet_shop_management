package model;

public class Customer extends Human {
    private int loyaltyPoints;

    // Constructor for new customer (insert mode)
    public Customer(String name, String email, String phone) {
        super(name, email, phone);
        this.loyaltyPoints = 0; // Start with 0 points
    }

    // Constructor for loading from DB
    public Customer(int id, String name, String email, String phone, int loyaltyPoints) {
        super(name, email, phone);
        setId(id); // setId is defined in Human, protected
        this.loyaltyPoints = loyaltyPoints;
    }

    // Getter
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    // Add points (used in BillingService)
    public void addLoyaltyPoints(int points) {
        if (points <= 0)
            throw new IllegalArgumentException("Invalid points");
        loyaltyPoints += points;
    }

    // Optional: reset points if needed
    public void resetLoyaltyPoints() {
        loyaltyPoints = 0;
    }
}
