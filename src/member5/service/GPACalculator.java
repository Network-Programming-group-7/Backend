package member5.service;

import common.models.Result;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPACalculator {
    
    public GPA calculateGPA(List<Result> results) {
        if (results == null || results.isEmpty()) {
            return new GPA(0.0, 0, 0);
        }
        
        double totalPoints = 0.0;
        int totalCredits = 0;
        
        for (Result result : results) {
            double gradePoint = getGradePoint(result.getGrade());
            int credits = result.getCredits() > 0 ? result.getCredits() : 3; // Default 3 credits
            
            totalPoints += gradePoint * credits;
            totalCredits += credits;
        }
        
        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
        gpa = Math.round(gpa * 100.0) / 100.0; // Round to 2 decimal places
        
        return new GPA(gpa, totalCredits, results.size());
    }
    
    public double getGradePoint(String grade) {
        if (grade == null) return 0.0;
        
        switch (grade.toUpperCase()) {
            case "A+":
            case "A":
                return 4.0;
            case "A-":
                return 3.7;
            case "B+":
                return 3.3;
            case "B":
                return 3.0;
            case "B-":
                return 2.7;
            case "C+":
                return 2.3;
            case "C":
                return 2.0;
            case "C-":
                return 1.7;
            case "D":
                return 1.0;
            case "F":
                return 0.0;
            default:
                return 0.0;
        }
    }
    
    public Map<String, Double> calculateSemesterWiseGPA(List<Result> results) {
        Map<String, Double> semesterGPAs = new HashMap<>();
        
        // Group results by semester (if semester info available)
        // For now, return overall GPA
        GPA overallGPA = calculateGPA(results);
        semesterGPAs.put("Overall", overallGPA.getValue());
        
        return semesterGPAs;
    }
    
    // Inner class for GPA result
    public static class GPA implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private double value;
        private int totalCredits;
        private int modulesCompleted;
        
        public GPA(double value, int totalCredits, int modulesCompleted) {
            this.value = value;
            this.totalCredits = totalCredits;
            this.modulesCompleted = modulesCompleted;
        }
        
        public double getValue() { return value; }
        public int getTotalCredits() { return totalCredits; }
        public int getModulesCompleted() { return modulesCompleted; }
        
        @Override
        public String toString() {
            return String.format("GPA: %.2f | Credits: %d | Modules: %d", 
                value, totalCredits, modulesCompleted);
        }
    }
}