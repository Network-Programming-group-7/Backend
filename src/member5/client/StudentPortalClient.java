package member5.client;

import common.models.Module;
import common.models.Result;
import common.network.LoginRequest;
import common.network.Request;
import common.network.Response;
import common.utils.Constants;
import member5.service.GPACalculator.GPA;
import member5.service.StudentSession.Notification;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Console client for students to access the portal
 * Connects to StudentPortalServer (Port 8005)
 */
public class StudentPortalClient {
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    private String studentId;
    private boolean running;
    
    // Notification listener thread
    private Thread notificationListener;
    
    public static void main(String[] args) {
        StudentPortalClient client = new StudentPortalClient();
        client.start();
    }
    
    public void start() {
        scanner = new Scanner(System.in);
        running = true;
        
        try {
            // Connect to server
            connect();
            
            // Login
            if (login()) {
                // Start notification listener
               // startNotificationListener();
                
                // Show menu
                showMenu();
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }
    
    private void connect() throws IOException {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   UNIVERSITY STUDENT PORTAL          ║");
        System.out.println("╚══════════════════════════════════════╝\n");
        
        System.out.println("🔗 Connecting to Student Portal Server...");
        socket = new Socket(Constants.SERVER_HOST, Constants.STUDENT_PORTAL_PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("✅ Connected to server at " + Constants.SERVER_HOST + ":" + Constants.STUDENT_PORTAL_PORT + "\n");
    }
    
    private boolean login() {
        try {
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║         STUDENT LOGIN                ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            System.out.print("Student ID: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            
            // Send login request
            LoginRequest loginRequest = new LoginRequest(username, password, Constants.ROLE_STUDENT);
            out.writeObject(loginRequest);
            out.flush();
            
            // Read response
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                this.studentId = username;
                System.out.println("\n✅ Login Successful!");
                System.out.println("Welcome, " + studentId + "!");
                
                // Wait a moment before showing menu
                Thread.sleep(1000);
                
                return true;
            } else {
                System.out.println("\n❌ Login Failed: " + response.getMessage());
                System.out.println("Please check your credentials and try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            return false;
        }
    }
    
    private void showMenu() {
        while (running) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║     STUDENT PORTAL - MAIN MENU       ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.println("  1. View My Results");
            System.out.println("  2. View Result by Module");
            System.out.println("  3. Calculate GPA");
            System.out.println("  4. Register for Module");
            System.out.println("  5. View Registered Modules");
            System.out.println("  6. View Available Modules");
            System.out.println("  7. Download Transcript");
            System.out.println("  8. Logout");
            System.out.println("═══════════════════════════════════════");
            System.out.print("Enter your choice (1-8): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    viewAllResults();
                    break;
                case "2":
                    viewModuleResult();
                    break;
                case "3":
                    calculateGPA();
                    break;
                case "4":
                    registerModule();
                    break;
                case "5":
                    viewRegisteredModules();
                    break;
                case "6":
                    viewAvailableModules();
                    break;
                case "7":
                    downloadTranscript();
                    break;
                case "8":
                    logout();
                    return;
                default:
                    System.out.println("\n❌ Invalid choice! Please enter a number between 1-8.");
            }
            
            // Pause before showing menu again
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    private void viewAllResults() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📊 FETCHING YOUR RESULTS...");
            System.out.println("═".repeat(70) + "\n");
            
            Request request = new Request(Constants.VIEW_MY_RESULTS, null);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                @SuppressWarnings("unchecked")
                List<Result> results = (List<Result>) response.getData();
                
                if (results.isEmpty()) {
                    System.out.println("❌ No results found.");
                    System.out.println("Results will appear here once your lecturers publish them.");
                } else {
                    displayResults(results);
                }
            } else {
                System.out.println("❌ Error: " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching results: " + e.getMessage());
        }
    }
    
    private void displayResults(List<Result> results) {
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           YOUR RESULTS                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println(String.format("%-12s %-30s %-8s %-6s %-8s",
            "MODULE", "NAME", "CREDITS", "MARKS", "GRADE"));
        System.out.println("─".repeat(70));
        
        int totalCredits = 0;
        int totalModules = 0;
        
        for (Result result : results) {
            String moduleName = result.getModuleName() != null ? result.getModuleName() : "N/A";
            if (moduleName.length() > 28) {
                moduleName = moduleName.substring(0, 25) + "...";
            }
            
            System.out.println(String.format("%-12s %-30s %-8d %-6d %-8s",
                result.getModuleCode(),
                moduleName,
                result.getCredits(),
                result.getMarks(),
                result.getGrade()));
            
            totalCredits += result.getCredits();
            totalModules++;
        }
        
        System.out.println("─".repeat(70));
        System.out.println(String.format("Total: %d modules | %d credits", totalModules, totalCredits));
        System.out.println("═".repeat(70));
    }
    
    private void viewModuleResult() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📊 VIEW MODULE RESULT");
            System.out.println("═".repeat(70));
            
            System.out.print("\nEnter module code (e.g., CS3111): ");
            String moduleCode = scanner.nextLine().trim().toUpperCase();
            
            if (moduleCode.isEmpty()) {
                System.out.println("❌ Module code cannot be empty!");
                return;
            }
            
            System.out.println("\n🔍 Searching for result...");
            
            Request request = new Request(Constants.VIEW_MODULE_RESULT, moduleCode);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                Result result = (Result) response.getData();
                
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║       MODULE RESULT DETAILS            ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.println();
                System.out.println("  Module Code:     " + result.getModuleCode());
                System.out.println("  Module Name:     " + (result.getModuleName() != null ? result.getModuleName() : "N/A"));
                System.out.println("  Credits:         " + result.getCredits());
                System.out.println("  Marks Obtained:  " + result.getMarks() + " / 100");
                System.out.println("  Grade:           " + result.getGrade());
                System.out.println("  Status:          " + (result.getMarks() >= 40 ? "✅ PASS" : "❌ FAIL"));
                System.out.println();
                System.out.println("─".repeat(42));
                
            } else {
                System.out.println("\n❌ " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void calculateGPA() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("🎓 CALCULATING YOUR GPA...");
            System.out.println("═".repeat(70) + "\n");
            
            Request request = new Request(Constants.CALCULATE_GPA, null);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                GPA gpa = (GPA) response.getData();
                
                System.out.println("╔════════════════════════════════════════╗");
                System.out.println("║         GPA CALCULATION RESULT         ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.println();
                System.out.println(String.format("  Cumulative GPA:       %.2f / 4.00", gpa.getValue()));
                System.out.println("  Total Credits Earned: " + gpa.getTotalCredits());
                System.out.println("  Modules Completed:    " + gpa.getModulesCompleted());
                System.out.println();
                System.out.println("─".repeat(42));
                
                // Show classification
                String classification = getClassification(gpa.getValue());
                System.out.println("  Classification:       " + classification);
                System.out.println();
                
                // Show GPA interpretation
                System.out.println("  GPA Interpretation:");
                if (gpa.getValue() >= 3.70) {
                    System.out.println("  🌟 Excellent Performance!");
                } else if (gpa.getValue() >= 3.00) {
                    System.out.println("  ✅ Good Performance!");
                } else if (gpa.getValue() >= 2.00) {
                    System.out.println("  ⚠️  Satisfactory - Room for improvement");
                } else {
                    System.out.println("  ⚠️  Below expectations - Need improvement");
                }
                
                System.out.println("═".repeat(42));
                
            } else {
                System.out.println("❌ " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error calculating GPA: " + e.getMessage());
        }
    }
    
    private String getClassification(double gpa) {
        if (gpa >= 3.70) return "First Class Honours";
        else if (gpa >= 3.30) return "Second Class Honours (Upper Division)";
        else if (gpa >= 3.00) return "Second Class Honours (Lower Division)";
        else if (gpa >= 2.00) return "Pass";
        else return "Fail";
    }
    
    private void registerModule() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📝 MODULE REGISTRATION");
            System.out.println("═".repeat(70));
            
            System.out.print("\nEnter module code to register (e.g., CS3115): ");
            String moduleCode = scanner.nextLine().trim().toUpperCase();
            
            if (moduleCode.isEmpty()) {
                System.out.println("❌ Module code cannot be empty!");
                return;
            }
            
            System.out.print("Are you sure you want to register for " + moduleCode + "? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("y")) {
                System.out.println("❌ Registration cancelled.");
                return;
            }
            
            System.out.println("\n⏳ Processing registration...");
            
            Request request = new Request(Constants.REGISTER_MODULE, moduleCode);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                System.out.println("\n✅ SUCCESS! You have been registered for " + moduleCode);
                System.out.println("You can now view this module in your registered modules list.");
            } else {
                System.out.println("\n❌ Registration Failed: " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void viewRegisteredModules() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📚 FETCHING YOUR REGISTERED MODULES...");
            System.out.println("═".repeat(70) + "\n");
            
            Request request = new Request(Constants.VIEW_REGISTERED_MODULES, null);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                @SuppressWarnings("unchecked")
                List<Module> modules = (List<Module>) response.getData();
                
                if (modules.isEmpty()) {
                    System.out.println("❌ No registered modules found.");
                    System.out.println("Register for modules using option 4 in the main menu.");
                } else {
                    displayModules(modules, "YOUR REGISTERED MODULES");
                }
            } else {
                System.out.println("❌ Error: " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void viewAvailableModules() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📚 FETCHING AVAILABLE MODULES...");
            System.out.println("═".repeat(70) + "\n");
            
            Request request = new Request(Constants.VIEW_AVAILABLE_MODULES, null);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                @SuppressWarnings("unchecked")
                List<Module> modules = (List<Module>) response.getData();
                
                if (modules.isEmpty()) {
                    System.out.println("❌ No available modules found.");
                    System.out.println("All modules may already be registered or registration is closed.");
                } else {
                    displayModules(modules, "AVAILABLE MODULES FOR REGISTRATION");
                    System.out.println("\n💡 Tip: Use option 4 to register for any of these modules.");
                }
            } else {
                System.out.println("❌ Error: " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void displayModules(List<Module> modules, String title) {
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  " + title + " ".repeat(Math.max(0, 66 - title.length())) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println(String.format("%-12s %-35s %-10s %-10s",
            "CODE", "NAME", "CREDITS", "SEMESTER"));
        System.out.println("─".repeat(70));
        
        for (Module module : modules) {
            String name = module.getName();
            if (name != null && name.length() > 33) {
                name = name.substring(0, 30) + "...";
            }
            
            String semester = module.getSemester() != null ? module.getSemester() : "N/A";
            
            System.out.println(String.format("%-12s %-35s %-10d %-10s",
                module.getCode(),
                name,
                module.getCredits(),
                semester));
        }
        
        System.out.println("─".repeat(70));
        System.out.println("Total modules: " + modules.size());
        System.out.println("═".repeat(70));
    }
    
    private void downloadTranscript() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("📄 GENERATING YOUR ACADEMIC TRANSCRIPT...");
            System.out.println("═".repeat(70) + "\n");
            
            Request request = new Request(Constants.DOWNLOAD_TRANSCRIPT, null);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                byte[] transcript = (byte[]) response.getData();
                String transcriptText = new String(transcript);
                
                // Display transcript
                System.out.println(transcriptText);
                
                // Ask to save to file
                System.out.println("\n" + "═".repeat(70));
                System.out.print("💾 Would you like to save this transcript to a file? (y/n): ");
                String save = scanner.nextLine().trim().toLowerCase();
                
                if (save.equals("y")) {
                    String filename = "Transcript_" + studentId + "_" + System.currentTimeMillis() + ".txt";
                    
                    try (FileOutputStream fos = new FileOutputStream(filename)) {
                        fos.write(transcript);
                        System.out.println("\n✅ SUCCESS! Transcript saved to: " + filename);
                        System.out.println("📁 File location: " + new File(filename).getAbsolutePath());
                    } catch (IOException e) {
                        System.err.println("❌ Error saving file: " + e.getMessage());
                    }
                } else {
                    System.out.println("\n✅ Transcript displayed only (not saved to file)");
                }
                
            } else {
                System.out.println("❌ Error: " + response.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void logout() {
        try {
            System.out.println("\n" + "═".repeat(70));
            System.out.println("👋 LOGGING OUT...");
            System.out.println("═".repeat(70));
            
            Request request = new Request("LOGOUT", null);
            out.writeObject(request);
            out.flush();
            
            // Wait for response
            try {
                Response response = (Response) in.readObject();
                if (response.getStatus().equals(Constants.SUCCESS)) {
                    System.out.println("\n✅ " + response.getMessage());
                }
            } catch (Exception e) {
                // Server may close connection immediately
            }
            
            running = false;
            
            System.out.println("Thank you for using Student Portal!");
            System.out.println("Have a great day, " + studentId + "! 👋\n");
            
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
    
    /**
     * Start a separate thread to listen for notifications from server
     */
    private void startNotificationListener() {
        notificationListener = new Thread(() -> {
            try {
                while (running) {
                    Object obj = in.readObject();
                    
                    // Check if it's a notification
                    if (obj instanceof Notification) {
                        Notification notification = (Notification) obj;
                        
                        // Display notification with timestamp
                        System.out.println("\n" + "═".repeat(70));
                        System.out.println("🔔 NEW NOTIFICATION");
                        System.out.println("═".repeat(70));
                        System.out.println("📅 Time: " + notification.getTimestamp());
                        System.out.println("📨 Message: " + notification.getMessage());
                        System.out.println("═".repeat(70));
                        System.out.print("Enter your choice (1-8): "); // Reprint prompt
                    }
                }
            } catch (EOFException e) {
                // Connection closed - normal during logout
            } catch (Exception e) {
                if (running) {
                    System.err.println("\n⚠️  Notification listener error: " + e.getMessage());
                }
            }
        });
        
        // Set as daemon thread so it doesn't prevent program exit
        notificationListener.setDaemon(true);
        notificationListener.start();
        
        System.out.println("✅ Notification listener started - You will receive real-time updates!");
    }
    
    /**
     * Clean up resources and close connections
     */
    private void disconnect() {
        try {
            running = false;
            
            // Stop notification listener
            if (notificationListener != null && notificationListener.isAlive()) {
                notificationListener.interrupt();
            }
            
            // Close streams and socket
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (scanner != null) scanner.close();
            
            System.out.println("\n✅ Disconnected from server successfully.");
            
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }
}