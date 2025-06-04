package model.user;

import model.user.Human;

public abstract class SysUser extends Human {
    private String username;
    private String passwordHash;

    public SysUser(String name, String email, String phone, String username, String passwordHash) {
        super(name, email, phone);
        setUsername(username);
        if (passwordHash != null && !passwordHash.isBlank()) {
            setPasswordHash(passwordHash); // only if valid
        }
    }

    // Getters
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    // Setters 
    public void setUsername(String username) {
        if (username == null || username.length() < 5) {
            throw new IllegalArgumentException("Invalid username");
        }
        this.username = username;
    }

    public void setPasswordHash(String hash) {
        if (hash == null || hash.length() != 60) { // BCrypt hash check
            throw new IllegalArgumentException("Invalid password hash");
        }
        this.passwordHash = hash;
    }
}