package dao.user;

import model.user.Manager;
import model.user.Staff;
import database.connection_provider;
import util.hash.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.user.SysUser;

public class UserDAO {
    private static final String TABLE = "staff";

    public boolean saveStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (name, email, phone, username, password_hash, role, salary) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getUsername());
            ps.setString(5, staff.getPasswordHash());
            ps.setString(6, "STAFF");
            ps.setBigDecimal(7, staff.getSalary());

            int rows = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) staff.setId(rs.getInt(1));
            }

            return rows > 0;
        }
    }

    public Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ? AND role = 'STAFF'";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getBigDecimal("salary")
                    );
                    staff.setId(rs.getInt("id"));
                    return staff;
                }
            }
        }
        return null;
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE role = 'STAFF'";

        try (Connection conn = connection_provider.getCon();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Staff staff = new Staff(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getBigDecimal("salary")
                );
                staff.setId(rs.getInt("id"));
                list.add(staff);
            }
        }

        return list;
    }
    
    public SysUser getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM staff WHERE email = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("MANAGER".equalsIgnoreCase(role)) {
                        Manager manager = new Manager(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("username"),
                            rs.getString("password_hash")
                        );
                        manager.setId(rs.getInt("id"));
                        return manager;
                    } else {
                        Staff staff = new Staff(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getBigDecimal("salary")
                        );
                        staff.setId(rs.getInt("id"));
                        return staff;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff WHERE email = ?";
        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET name = ?, email = ?, phone = ?, username = ?, password_hash = ?, salary = ? WHERE id = ? AND role = 'STAFF'";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getUsername());
            ps.setString(5, staff.getPasswordHash());
            ps.setBigDecimal(6, staff.getSalary());
            ps.setInt(7, staff.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(SysUser user) throws SQLException {
        String sql = "UPDATE staff SET name = ?, email = ?, phone = ?, username = ?, password_hash = ? WHERE id = ?";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPasswordHash());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean deleteStaff(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ? AND role = 'STAFF'";

        try (Connection conn = connection_provider.getCon();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
