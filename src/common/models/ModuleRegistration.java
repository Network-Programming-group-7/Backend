package common.models;

import java.io.Serializable;
import java.util.Date;

public class ModuleRegistration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String moduleCode;
    private Date registrationDate;
    private String status; // ACTIVE, DROPPED
    
    public ModuleRegistration() {
        this.registrationDate = new Date();
        this.status = "ACTIVE";
    }
    
    public ModuleRegistration(String studentId, String moduleCode) {
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.registrationDate = new Date();
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Registration{student='" + studentId + "', module='" + moduleCode + "', status='" + status + "'}";
    }
}