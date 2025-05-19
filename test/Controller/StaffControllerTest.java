package Controller;

import controller.AuthController;
import controller.StaffController;
import dao.UserDAO;
import model.Manager;
import model.Staff;
import org.junit.Test;
import util.BCrypt;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class StaffControllerTest {

    @Test
    public void testOnlyManagerCanUpdateSalary() throws Exception {
        Staff staffUser = new Staff("Staff1", "s1@mail.com", "0000000000", "staff1", new BigDecimal("1200"));
        staffUser.setPasswordHash(BCrypt.hashpw("staffpass", BCrypt.gensalt()));
        AuthController.currentUser = staffUser;
        
        Staff target = new Staff("Target", "target@mail.com", "1111111111", "target", new BigDecimal("1200"));
        target.setPasswordHash(BCrypt.hashpw("targetpass", BCrypt.gensalt()));
        
        UserDAO dao = new UserDAO();
        dao.saveStaff(target);
        
        StaffController controller = new StaffController();
        // Staff user tries to update another staff's salary
        target.setSalary(new BigDecimal("1500"));
        boolean result1 = controller.updateStaff(target);
        assertFalse("Non-manager should not be able to update salary", result1);

        // Simulate a MANAGER login
        Manager manager = new Manager("Boss", "boss@mail.com", "9999999999", "bossname", BCrypt.hashpw("bosspass", BCrypt.gensalt()));
        AuthController.currentUser = manager;

        // Manager updates the salary
        target.setSalary(new BigDecimal("1800"));
        boolean result2 = controller.updateStaff(target);
        assertTrue("Manager should be able to update salary", result2);
    }
}
