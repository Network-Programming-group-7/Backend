package member5.server;

import member5.service.StudentSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Broadcast notifications to all or specific students
 */
public class NotificationBroadcaster {
    
    private Map<String, StudentSession> activeSessions;
    
    public NotificationBroadcaster(Map<String, StudentSession> activeSessions) {
        this.activeSessions = activeSessions;
    }
    
    /**
     * Broadcast message to all online students
     */
    public void broadcastToAll(String message) {
        int sentCount = 0;
        
        System.out.println("📢 Broadcasting to all students: " + message);
        
        for (StudentSession session : activeSessions.values()) {
            if (session.isOnline()) {
                session.sendNotification(message);
                sentCount++;
            }
        }
        
        System.out.println("✅ Broadcast sent to " + sentCount + " students");
    }
    
    /**
     * Notify specific student
     */
    public boolean notifyStudent(String studentId, String message) {
        StudentSession session = activeSessions.get(studentId);
        
        if (session != null && session.isOnline()) {
            session.sendNotification(message);
            System.out.println("📧 Notified student " + studentId + ": " + message);
            return true;
        } else {
            System.out.println("⚠️ Student " + studentId + " is not online");
            return false;
        }
    }
    
    /**
     * Notify multiple students
     */
    public void notifyMultiple(List<String> studentIds, String message) {
        int sentCount = 0;
        
        System.out.println("📧 Notifying " + studentIds.size() + " students: " + message);
        
        for (String studentId : studentIds) {
            if (notifyStudent(studentId, message)) {
                sentCount++;
            }
        }
        
        System.out.println("✅ Notification sent to " + sentCount + "/" + studentIds.size() + " students");
    }
    
    /**
     * Notify all students in a batch
     * (Would connect to Member 1's server to get batch students)
     */
    public void notifyBatch(String batchId, String message) {
        // TODO: Get students in batch from Member 1's server
        // For now, broadcast to all
        broadcastToAll("[Batch " + batchId + "] " + message);
    }
    
    /**
     * Get list of online students
     */
    public List<String> getOnlineStudents() {
        List<String> onlineStudents = new ArrayList<>();
        
        for (Map.Entry<String, StudentSession> entry : activeSessions.entrySet()) {
            if (entry.getValue().isOnline()) {
                onlineStudents.add(entry.getKey());
            }
        }
        
        return onlineStudents;
    }
    
    /**
     * Get count of online students
     */
    public int getOnlineCount() {
        return getOnlineStudents().size();
    }
    
    /**
     * Notify when new results are published
     */
    public void notifyResultsPublished(String moduleCode) {
        String message = "🎉 New results published for module: " + moduleCode + "! Check your results now.";
        broadcastToAll(message);
    }
    
    /**
     * Notify about upcoming exam
     */
    public void notifyUpcomingExam(String moduleCode, String date) {
        String message = "📝 Reminder: Exam for " + moduleCode + " on " + date;
        broadcastToAll(message);
    }
    
    /**
     * Notify about new module registration period
     */
    public void notifyRegistrationOpen() {
        String message = "📚 Module registration is now open! Register for your modules.";
        broadcastToAll(message);
    }
}