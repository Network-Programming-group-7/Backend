package common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Batch implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String year;
    private String semester;
    private List<String> studentIds;
    private Date createdDate;
    
    public Batch() {
        this.studentIds = new ArrayList<>();
        this.createdDate = new Date();
    }
    
    public Batch(String id, String name, String year, String semester) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.semester = semester;
        this.studentIds = new ArrayList<>();
        this.createdDate = new Date();
    }
    
    public void addStudent(String studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }
    
    public void removeStudent(String studentId) {
        studentIds.remove(studentId);
    }
    
    public int getStudentCount() {
        return studentIds.size();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public List<String> getStudentIds() { return studentIds; }
    public void setStudentIds(List<String> studentIds) { this.studentIds = studentIds; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    @Override
    public String toString() {
        return "Batch{id='" + id + "', name='" + name + "', students=" + studentIds.size() + "}";
    }
}