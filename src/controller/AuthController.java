package controller;

import dao.UserDAO;
import model.SysUser;
import model.Staff;
import model.Manager;
import util.BCrypt;
import java.sql.*;

public class AuthController {
    private final UserDAO userDao = new UserDAO();
    public static SysUser currentUser;

    public boolean login(String email, String password) {
        try {
            SysUser user = userDao.getByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                currentUser = user;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean signup(Staff staff, String rawPassword) {
        try {
            staff.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            return userDao.saveStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isManager() {
        return currentUser instanceof Manager;
    }
}

