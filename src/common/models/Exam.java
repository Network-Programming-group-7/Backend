package common.models;

import java.io.Serializable;
import java.util.Date;

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String moduleCode;
    private String batchId;
    private Date date;
    private String startTime;
    private String endTime;
    private String venue;
    private int maxMarks;
    private String type;
    
    public Exam() {
        this.maxMarks = 100;
    }
    
    public Exam(String id, String moduleCode, String batchId) {
        this.id = id;
        this.moduleCode = moduleCode;
        this.batchId = batchId;
        this.maxMarks = 100;
    }
    
    public Exam(String id, String moduleCode, String batchId, Date date, 
                String startTime, String endTime, String venue, int maxMarks, String type) {
        this.id = id;
        this.moduleCode = moduleCode;
        this.batchId = batchId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.maxMarks = maxMarks;
        this.type = type;
    }
    
    public boolean isConflictWith(Exam other) {
        if (!this.batchId.equals(other.batchId)) {
            return false;
        }
        if (!this.date.equals(other.date)) {
            return false;
        }
        return this.startTime.equals(other.startTime);
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    @Override
    public String toString() {
        return "Exam{id='" + id + "', module='" + moduleCode + "', date=" + date + "}";
    }
}