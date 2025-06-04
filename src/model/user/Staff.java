package model.user;

import model.user.SysUser;
import java.math.BigDecimal;

public class Staff extends SysUser {
    private BigDecimal salary;

    public Staff(String name, String email, String phone, String username, String passwordHash, BigDecimal salary) {
        super(name, email, phone, username, passwordHash);
        setSalary(salary);
    }
    
    // allow null hash
    public Staff(String name, String email, String phone, String username, BigDecimal salary) {
        super(name, email, phone, username, null); // hash is null for now
        setSalary(salary);
    }


    public BigDecimal getSalary() { return salary; }
    
    // Package-private for Manager to modify
    public void setSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Invalid salary");
        this.salary = salary;
    }
}