package Service;

import model.user.Staff;
import model.user.SysUser;
import org.junit.Test;
import service.auth.AuthService;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class AuthServiceTest {

    @Test
    public void testRegisterAndLogin() {
        AuthService auth = new AuthService();
        
        Staff staff = new Staff("Test User", "test@example.com", "0123456789", "testuser", new BigDecimal(1000));

        boolean registered = auth.registerStaff(staff, "password123");
        assertTrue("Registration should succeed", registered);

        SysUser loggedIn = auth.authenticate("test@example.com", "password123");
        assertNotNull(loggedIn);
        assertEquals("testuser", loggedIn.getUsername());
    }
}

