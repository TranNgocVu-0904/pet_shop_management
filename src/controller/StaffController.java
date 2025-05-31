package controller;

import dao.UserDAO;
import model.Manager;
import model.Staff;
import controller.AuthController;
import java.util.List;
import java.sql.*;
import model.SysUser;

public class StaffController {
    private final UserDAO userDao = new UserDAO();

    public List<Staff> getAllStaff() {
        try {
            return userDao.getAllStaff();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean addStaff(Staff staff) {
        try {
            return userDao.saveStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStaff(Staff staff) {
        if (!(AuthController.currentUser instanceof Manager)) return false;

        try {
            return userDao.updateStaff(staff);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStaff(int id) {
        if (!(AuthController.currentUser instanceof Manager)) return false;

        try {
            return userDao.deleteStaff(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUser(SysUser user) {
        try {
            return userDao.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
