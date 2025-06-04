package dao.customer;

import model.user.Customer;
import database.connection_provider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String TABLE = "customers";

    public Customer saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (name, email, phone, loyalty_points) VALUES (?, ?, ?, ?)";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setInt(4, customer.getLoyaltyPoints());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) customer.setId(rs.getInt(1));
            }

            return customer;
        }
    }

    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("loyalty_points")
                    );
                }
            }
        }

        return null;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET name = ?, email = ?, phone = ?, loyalty_points = ? WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setInt(4, customer.getLoyaltyPoints());
            ps.setInt(5, customer.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE;

        try (Connection conn = connection_provider.getCon();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("loyalty_points")
                ));
            }
        }

        return customers;
    }

    public List<Customer> getByLoyaltyPoints(String order) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " ORDER BY loyalty_points " +
                     ("DESC".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (Connection conn = connection_provider.getCon();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("loyalty_points")
                ));
            }
        }

        return customers;
    }
}
