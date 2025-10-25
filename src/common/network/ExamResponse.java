package common.network;

import common.models.Exam;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ExamResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private Exam exam;
    private List<Exam> exams;
    private List<String> conflicts;
    private Map<String, Integer> statistics;
    
    public ExamResponse() {}
    
    public ExamResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ExamResponse(boolean success, String message, Exam exam) {
        this.success = success;
        this.message = message;
        this.exam = exam;
    }
    
    public ExamResponse(boolean success, String message, List<Exam> exams) {
        this.success = success;
        this.message = message;
        this.exams = exams;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Exam getExam() {
        return exam;
    }
    
    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
    public List<Exam> getExams() {
        return exams;
    }
    
    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
    
    public void setConflicts(List<String> conflicts) {
        this.conflicts = conflicts;
    }
    
    public Map<String, Integer> getStatistics() {
        return statistics;
    }
    
    public void setStatistics(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }
}