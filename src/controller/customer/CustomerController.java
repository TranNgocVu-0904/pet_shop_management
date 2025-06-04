package controller.customer;

import dao.customer.CustomerDAO;
import model.user.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private static final CustomerDAO customerDao = new CustomerDAO();

    public static boolean addCustomer(Customer customer) {
        try {
            customerDao.saveCustomer(customer);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(Customer customer) {
        try {
            return customerDao.updateCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Customer getCustomerById(int id) {
        try {
            return customerDao.getCustomerById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Customer> getAllCustomers() {
        try {
            return customerDao.getAllCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<Customer> getCustomersByLoyalty(String order) {
        try {
            return customerDao.getByLoyaltyPoints(order);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
