package member5.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class StudentSession {
    private String studentId;
    private String sessionId;
    private ObjectOutputStream out;
    private Date loginTime;
    private boolean online;
    
    public StudentSession(String studentId, ObjectOutputStream out) {
        this.studentId = studentId;
        this.sessionId = UUID.randomUUID().toString();
        this.out = out;
        this.loginTime = new Date();
        this.online = true;
    }
    
    public void sendNotification(String message) {
        try {
            Notification notification = new Notification(message, new Date());
            out.writeObject(notification);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send notification to student: " + studentId);
            online = false;
        }
    }
    
    public void sendResponse(Object response) {
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send response to student: " + studentId);
            online = false;
        }
    }
    
    public void logout() {
        this.online = false;
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters
    public String getStudentId() { return studentId; }
    public String getSessionId() { return sessionId; }
    public boolean isOnline() { return online; }
    public Date getLoginTime() { return loginTime; }
    
    // Inner class for Notification
    public static class Notification implements Serializable {
        private static final long serialVersionUID = 1L;
        private String message;
        private Date timestamp;
        
        public Notification(String message, Date timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getMessage() { return message; }
        public Date getTimestamp() { return timestamp; }
    }
}