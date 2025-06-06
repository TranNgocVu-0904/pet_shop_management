package dao.bill;

import model.billing.Bill;
import model.billing.BillItem;
import database.connection_provider;
import java.math.BigDecimal;

import java.sql.*;

public class BillDAO {
    public static boolean createBill(Bill bill, java.util.List<BillItem> items) {
        try (Connection conn = connection_provider.getCon()) {
            conn.setAutoCommit(false);

            String insertBill = "INSERT INTO bills (customer_id, staff_id, total_amount, payment_method, transaction_time) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bill.getCustomerId());
                ps.setInt(2, bill.getStaffId());
                ps.setBigDecimal(3, bill.getTotalAmount());
                ps.setString(4, bill.getPaymentMethod());
                ps.setTimestamp(5, Timestamp.valueOf(bill.getTransactionTime()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setId(rs.getInt(1));
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
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Home panel
    public BigDecimal getTotalRevenue() {
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(total_amount) FROM bills");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public int getTotalOrderCount() {
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM bills");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
