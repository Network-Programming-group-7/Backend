package common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String email;
    private String batchId;
    private String phoneNumber;
    private Date enrollmentDate;
    private List<String> registeredModules;
    
    public Student() {
        this.registeredModules = new ArrayList<>();
        this.enrollmentDate = new Date();
    }
    
    public Student(String id, String name, String email, String batchId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.batchId = batchId;
        this.registeredModules = new ArrayList<>();
        this.enrollmentDate = new Date();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public List<String> getRegisteredModules() { return registeredModules; }
    public void setRegisteredModules(List<String> registeredModules) { this.registeredModules = registeredModules; }
    
    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', batchId='" + batchId + "'}";
    }
}