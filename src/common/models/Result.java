package common.models;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String studentId;
    private String moduleCode;
    private String moduleName;
    private String examId;
    private int marks;
    private String grade;
    private int credits;
    private String lecturerId;
    private Date uploadDate;
    private boolean isPublished;
    
    public Result() {
        this.uploadDate = new Date();
        this.isPublished = false;
        this.credits = 3; // Default
    }
    
    public Result(String studentId, String moduleCode, int marks) {
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.marks = marks;
        this.uploadDate = new Date();
        this.isPublished = false;
        this.credits = 3;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }
    
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }
    
    public Date getUploadDate() { return uploadDate; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }
    
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
    
    @Override
    public String toString() {
        return "Result{student='" + studentId + "', module='" + moduleCode + 
               "', marks=" + marks + ", grade='" + grade + "'}";
    }
}