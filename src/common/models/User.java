package common.models;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String password;
    private String role; // ADMIN, LECTURER, STUDENT
    private Date lastLogin;
    private boolean isActive;
    
    public User() {
        this.isActive = true;
    }
    
    public User(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }
    
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}