package common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lecturer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String password;
    private String name;
    private String email;
    private String department;
    private String phoneNumber;
    private List<String> moduleIds;
    private Date joinDate;
    
    public Lecturer() {
        this.moduleIds = new ArrayList<>();
        this.joinDate = new Date();
    }
    

    public Lecturer(String id, String name, String email, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.moduleIds = new ArrayList<>();
        this.joinDate = new Date();
    }
    

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public List<String> getModuleIds() { return moduleIds; }
    public void setModuleIds(List<String> moduleIds) { this.moduleIds = moduleIds; }
    
    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }
    
    @Override
    public String toString() {
        return "Lecturer{id='" + id + "', name='" + name + "', department='" + department + "'}";
    }
}