package model;

import java.math.BigDecimal;

public class Manager extends SysUser {
    public Manager(String name, String email, String phone, String username, String passwordHash) {
        super(name, email, phone, username, passwordHash);
    }

    // Special method only for managers
    public void adjustStaffSalary(Staff staff, BigDecimal newSalary) {
        staff.setSalary(newSalary);
    }
}