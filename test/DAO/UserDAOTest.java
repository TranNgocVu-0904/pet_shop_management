package DAO;

import dao.UserDAO;
import model.Staff;
import org.junit.Test;
import util.BCrypt;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class UserDAOTest {

    @Test
    public void testCreateAndCheckEmail() throws Exception {
        UserDAO dao = new UserDAO();
        
        Staff staff = new Staff("Alice", "alice@example.com", "0123456789", "alice123", new BigDecimal("2000"));
        staff.setPasswordHash(BCrypt.hashpw("staffpass", BCrypt.gensalt())); // valid 60-char bcrypt

        boolean saved = dao.saveStaff(staff);
        assertTrue(saved);

        boolean exists = dao.emailExists("alice@example.com");
        assertTrue(exists);
    }
}
