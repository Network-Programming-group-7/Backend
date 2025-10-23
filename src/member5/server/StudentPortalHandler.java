package member5.server;

import common.models.Module;
import common.models.Result;
import common.network.LoginRequest;
import common.network.Request;
import common.network.Response;
import common.utils.Constants;
import member5.service.*;
import member5.service.GPACalculator.GPA;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class StudentPortalHandler implements Runnable {
    
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String studentId;
    private StudentSession session;
    
    // Services
    private StudentAuthService authService;
    private ResultViewService resultViewService;
    private GPACalculator gpaCalculator;
    private TranscriptGenerator transcriptGenerator;
    
    // Active sessions map
    private java.util.Map<String, StudentSession> activeSessions;
    
    public StudentPortalHandler(Socket socket, 
                                StudentAuthService authService,
                                java.util.Map<String, StudentSession> activeSessions) {
        this.clientSocket = socket;
        this.authService = authService;
        this.activeSessions = activeSessions;
        
        // Initialize services
        this.resultViewService = new ResultViewService();
        this.gpaCalculator = new GPACalculator();
        this.transcriptGenerator = new TranscriptGenerator();
    }
    
    @Override
public void run() {
    try {
        // Setup streams - CRITICAL ORDER!
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
        
        System.out.println("👤 New student connection from: " + 
            clientSocket.getInetAddress().getHostAddress());
        
        // Authentication
        if (!authenticate()) {
            System.out.println("❌ Authentication failed");
            clientSocket.close();
            return;
        }
        
        System.out.println("✅ Student " + studentId + " authenticated successfully");
        
        // Create session
        session = new StudentSession(studentId, out);
        activeSessions.put(studentId, session);
        
        // REMOVED: session.sendNotification("Welcome to Student Portal!");
        // Reason: Client expects Response objects during menu operations
        
        // Handle requests
        handleRequests();
        
    } catch (IOException e) {
        System.err.println("❌ Connection error for student: " + studentId);
        e.printStackTrace();
    } finally {
        cleanup();
    }
}
    private boolean authenticate() throws IOException {
        try {
            // Read login request
            LoginRequest loginRequest = (LoginRequest) in.readObject();
            
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            
            System.out.println("🔐 Login attempt: " + username);
            
            // Validate credentials
            if (authService.authenticate(username, password)) {
                this.studentId = username;
                
                // Send success response
                Response response = Response.success("Login successful");
                out.writeObject(response);
                out.flush();
                
                return true;
            } else {
                // Send failure response
                Response response = Response.error("Invalid credentials");
                out.writeObject(response);
                out.flush();
                
                return false;
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error reading login request");
            e.printStackTrace();
            return false;
        }
    }
    
    private void handleRequests() {
        try {
            while (session.isOnline()) {
                try {
                    // Read request from student
                    Request request = (Request) in.readObject();
                    
                    System.out.println("📨 Request from " + studentId + ": " + request.getType());
                    
                    // Process request
                    Response response = processRequest(request);
                    
                    // Send response
                    System.out.println("📤 Sending response to " + studentId + "...");
                    out.writeObject(response);
                    out.flush();
                    System.out.println("✅ Response sent successfully");
                    
                } catch (ClassNotFoundException e) {
                    System.err.println("❌ Unknown object received from student: " + studentId);
                    e.printStackTrace();
                    Response errorResponse = Response.error("Invalid request format");
                    out.writeObject(errorResponse);
                    out.flush();
                }
            }
        } catch (EOFException e) {
            System.out.println("👋 Student " + studentId + " disconnected normally");
        } catch (IOException e) {
            if (session != null && session.isOnline()) {
                System.err.println("❌ Connection error for student: " + studentId);
                e.printStackTrace();
            }
        }
    }
    
    private Response processRequest(Request request) {
        String type = request.getType();
        
        try {
            System.out.println("🔄 Processing request type: " + type);
            
            switch (type) {
                case Constants.VIEW_MY_RESULTS:
                    return handleViewResults();
                
                case Constants.VIEW_MODULE_RESULT:
                    String moduleCode = (String) request.getData();
                    return handleViewModuleResult(moduleCode);
                
                case Constants.CALCULATE_GPA:
                    return handleCalculateGPA();
                
                case Constants.REGISTER_MODULE:
                    String modCode = (String) request.getData();
                    return handleRegisterModule(modCode);
                
                case Constants.VIEW_REGISTERED_MODULES:
                    return handleViewRegisteredModules();
                
                case Constants.VIEW_AVAILABLE_MODULES:
                    return handleViewAvailableModules();
                
                case Constants.DOWNLOAD_TRANSCRIPT:
                    return handleDownloadTranscript();
                
                case "LOGOUT":
                    session.logout();
                    return Response.success("Logged out successfully");
                
                default:
                    System.err.println("⚠️ Unknown request type: " + type);
                    return Response.error("Unknown request type: " + type);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing request: " + type);
            e.printStackTrace();
            return Response.error("Server error: " + e.getMessage());
        }
    }
    
    private Response handleViewResults() {
        try {
            System.out.println("📊 Fetching results for student: " + studentId);
            
            List<Result> results = resultViewService.getStudentResults(studentId);
            
            System.out.println("✅ Found " + results.size() + " results");
            
            if (results.isEmpty()) {
                return Response.error("No results found");
            }
            
            System.out.println("✅ Preparing to send results...");
            return Response.success(results);
            
        } catch (Exception e) {
            System.err.println("❌ Error in handleViewResults: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error fetching results: " + e.getMessage());
        }
    }
    
    private Response handleViewModuleResult(String moduleCode) {
        try {
            System.out.println("📊 Fetching result for module: " + moduleCode);
            
            Result result = resultViewService.getModuleResult(studentId, moduleCode);
            
            if (result == null) {
                return Response.error("Result not found for module: " + moduleCode);
            }
            
            return Response.success(result);
            
        } catch (Exception e) {
            System.err.println("❌ Error in handleViewModuleResult: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private Response handleCalculateGPA() {
        try {
            System.out.println("🎓 Calculating GPA for student: " + studentId);
            
            List<Result> results = resultViewService.getStudentResults(studentId);
            
            if (results.isEmpty()) {
                return Response.error("No results available to calculate GPA");
            }
            
            GPA gpa = gpaCalculator.calculateGPA(results);
            
            System.out.println("✅ GPA calculated: " + gpa.getValue());
            return Response.success(gpa);
            
        } catch (Exception e) {
            System.err.println("❌ Error calculating GPA: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private Response handleRegisterModule(String moduleCode) {
        try {
            System.out.println("📝 Registering module: " + moduleCode + " for student: " + studentId);
            
            boolean registered = resultViewService.registerForModule(studentId, moduleCode);
            
            if (registered) {
                return Response.success("Module registered successfully");
            } else {
                return Response.error("Module registration failed");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error registering module: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private Response handleViewRegisteredModules() {
        try {
            System.out.println("📚 Fetching registered modules for student: " + studentId);
            
            List<Module> modules = resultViewService.getRegisteredModules(studentId);
            
            return Response.success(modules);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching registered modules: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private Response handleViewAvailableModules() {
        try {
            System.out.println("📚 Fetching available modules for student: " + studentId);
            
            List<Module> modules = resultViewService.getAvailableModules(studentId);
            
            return Response.success(modules);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching available modules: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private Response handleDownloadTranscript() {
        try {
            System.out.println("📄 Generating transcript for student: " + studentId);
            
            List<Result> results = resultViewService.getStudentResults(studentId);
            
            if (results.isEmpty()) {
                return Response.error("No results available for transcript");
            }
            
            byte[] transcript = transcriptGenerator.generateTranscript(studentId, results);
            
            return Response.success(transcript);
            
        } catch (Exception e) {
            System.err.println("❌ Error generating transcript: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error: " + e.getMessage());
        }
    }
    
    private void cleanup() {
        try {
            if (studentId != null) {
                activeSessions.remove(studentId);
                System.out.println("🧹 Cleaned up session for student: " + studentId);
            }
            
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}