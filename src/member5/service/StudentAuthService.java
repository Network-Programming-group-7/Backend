package member5.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StudentAuthService {
    
    // In-memory storage (for now, later can be file-based)
    private Map<String, StudentCredential> studentCredentials;
    private Map<String, String> activeSessions; // sessionId -> studentId
    
    public StudentAuthService() {
        this.studentCredentials = new HashMap<>();
        this.activeSessions = new HashMap<>();
        
        // Add some test students
        addTestStudents();
    }
    
    private void addTestStudents() {
        // Test data - Remove in production
        studentCredentials.put("S001", new StudentCredential("S001", "password123"));
        studentCredentials.put("S002", new StudentCredential("S002", "password123"));
        studentCredentials.put("S003", new StudentCredential("S003", "password123"));
        studentCredentials.put("S004", new StudentCredential("S004", "password123"));
        studentCredentials.put("S005", new StudentCredential("S005", "password123"));
    }
    
    public boolean authenticate(String studentId, String password) {
        StudentCredential credential = studentCredentials.get(studentId);
        if (credential != null) {
            return credential.getPassword().equals(password);
        }
        return false;
    }
    
    public String createSession(String studentId) {
        String sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, studentId);
        return sessionId;
    }
    
    public boolean validateSession(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }
    
    public String getStudentIdFromSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
    
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
    
    public void addStudent(String studentId, String password) {
        studentCredentials.put(studentId, new StudentCredential(studentId, password));
    }
    
    // Inner class for credentials
    private static class StudentCredential {
        private String studentId;
        private String password;
        
        public StudentCredential(String studentId, String password) {
            this.studentId = studentId;
            this.password = password;
        }
        
    @SuppressWarnings("unused")
    public String getStudentId() { return studentId; }
        public String getPassword() { return password; }
    }
}