package member5.server;

import common.utils.Constants;
import member5.service.StudentAuthService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import member5.service.StudentSession;

public class StudentPortalServer {
    
    private static final int PORT = Constants.STUDENT_PORTAL_PORT;
    private static boolean running = true;
    private static ServerSocket serverSocket;
    
    // Services
    private static StudentAuthService authService;
    
    // Active sessions
    private static Map<String, StudentSession> activeSessions = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Student Portal Server Starting...    ");
        System.out.println("========================================");
        
        // Initialize services
        authService = new StudentAuthService();
        
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("✅ Server started successfully!");
            System.out.println("📡 Listening on port: " + PORT);
            System.out.println("⏰ Started at: " + new java.util.Date());
            System.out.println("========================================\n");
            
            // Accept client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔗 New connection from: " + 
                        clientSocket.getInetAddress().getHostAddress());
                    
                    // Create handler thread for this client
                    // This will use the REAL StudentPortalHandler from member5.server package
                    StudentPortalHandler handler = new StudentPortalHandler(
                        clientSocket, authService, activeSessions
                    );
                    Thread thread = new Thread(handler);
                    thread.start();
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }
    
    public static void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("\n📴 Server stopped.");
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }
    
    // Method to broadcast notifications to all online students
    public static void notifyAllStudents(String message) {
        System.out.println("📢 Broadcasting to " + activeSessions.size() + " students: " + message);
        for (StudentSession session : activeSessions.values()) {
            if (session.isOnline()) {
                session.sendNotification(message);
            }
        }
    }
    
    // Method to notify specific student
    public static void notifyStudent(String studentId, String message) {
        StudentSession session = activeSessions.get(studentId);
        if (session != null && session.isOnline()) {
            session.sendNotification(message);
            System.out.println("📧 Notified student " + studentId + ": " + message);
        }
    }
}