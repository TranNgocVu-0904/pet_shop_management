package service.human;

public class CustomerService {

    public void validateCustomerData(String name, String email, String phone, int loyalty) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("Email is required");
        if (phone == null || phone.isEmpty()) throw new IllegalArgumentException("Phone is required");
        if (loyalty < 0) throw new IllegalArgumentException("Loyalty points cannot be negative");
    }
}
