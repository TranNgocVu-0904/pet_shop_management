package controller.customer;

import dao.customer.CustomerDAO;
import model.user.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    private static final CustomerDAO customerDao = new CustomerDAO();

    // Thêm khách hàng với validate đơn giản
    public static boolean addCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getName().isBlank()
                || customer.getEmail() == null || customer.getEmail().isBlank()) {
            System.err.println("[ERROR] Invalid customer data");
            return false;
        }
        try {
            customerDao.saveCustomer(customer);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả khách hàng
    public static List<Customer> getAllCustomers() {
        try {
            return customerDao.getAllCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Lấy khách hàng theo ID trả về Optional
    public static Optional<Customer> getCustomerById(int id) {
        try {
            return customerDao.getCustomerById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Lấy danh sách khách hàng sắp xếp theo điểm loyalty (ASC hoặc DESC)
    public static List<Customer> getCustomersByLoyalty(String order) {
        try {
            return customerDao.getByLoyaltyPoints(order);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Cập nhật khách hàng với validate ID
    public static boolean updateCustomer(Customer customer) {
        if (customer == null || customer.getId() <= 0) {
            System.err.println("[ERROR] Invalid customer for update");
            return false;
        }
        try {
            return customerDao.updateCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa khách hàng theo ID
    public static boolean deleteCustomer(int customerId) {
        if (customerId <= 0) {
            System.err.println("[ERROR] Invalid customer ID for deletion");
            return false;
        }
        try {
            return customerDao.deleteCustomer(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
