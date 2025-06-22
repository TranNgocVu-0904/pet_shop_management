package dao.bill;

import model.billing.Bill;
import model.billing.BillItem;

import database.connection_provider;

import java.math.BigDecimal;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.util.List;

public class BillDAO {
    public static boolean createBill(Bill bill, List<BillItem> items) {
        if (bill == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Bill and items must not be null or empty");
        }

        Connection conn = null;
        try {
            conn = connection_provider.getCon();
            conn.setAutoCommit(false);

            String insertBill = "INSERT INTO bills (customer_id, staff_id, total_amount, payment_method, transaction_time) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bill.getCustomerId());
                ps.setInt(2, bill.getStaffId());
                ps.setBigDecimal(3, bill.getTotalAmount());
                ps.setString(4, bill.getPaymentMethod());
                ps.setTimestamp(5, Timestamp.valueOf(bill.getTransactionTime()));

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating bill failed, no rows affected.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Creating bill failed, no ID obtained.");
                    }
                }
            }

            String insertItem = "INSERT INTO bill_items (bill_id, item_type, pet_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (BillItem item : items) {
                    ps.setInt(1, bill.getId());
                    ps.setString(2, item.getItemType().name());

                    if (item.getItemType() == BillItem.ItemType.PRODUCT) {
                        ps.setNull(3, Types.INTEGER);
                        ps.setInt(4, item.getProductId());
                    } else {
                        ps.setInt(3, item.getPetId());
                        ps.setNull(4, Types.INTEGER);
                    }
                    ps.setInt(5, item.getQuantity());
                    ps.setBigDecimal(6, item.getUnitPrice());

                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);

            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Home panel - tổng doanh thu
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills WHERE status = 1";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;

        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // Home panel - tổng số đơn hàng
    public int getTotalOrderCount() {
        String sql = "SELECT COUNT(*) FROM bills WHERE status = 1";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
        public boolean softDeleteBill(int billId) {
        if (billId <= 0) return false;

        String sql = "UPDATE bills SET status = 0 WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
