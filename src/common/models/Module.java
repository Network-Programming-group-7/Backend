package common.models;

import java.io.Serializable;

public class Module implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private int credits;
    private String lecturerId;
    private String semester;
    private String description;
    private int maxStudents;
    
    public Module() {
        this.maxStudents = 100; // Default
    }
    
    public Module(String code, String name, int credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.maxStudents = 100;
    }
    
    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
    
    @Override
    public String toString() {
        return "Module{code='" + code + "', name='" + name + "', credits=" + credits + "}";
    }
}