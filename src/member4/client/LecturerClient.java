package member4.client;

import common.network.Request;
import common.network.Response;
import common.network.LoginRequest;
import common.models.Result;
import common.utils.Constants; // Assumed to contain RESULT_SERVER_PORT = 8004

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class LecturerClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = Constants.RESULT_PORT;

    public static void main(String[] args) {
        new LecturerClient().startClient();
    }

    public void startClient() {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                // Output stream MUST be created first!
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("✅ Connected to Result Management Server.");

            // Start the interactive test loop
            runTestMenu(oos, ois, scanner);

        } catch (Exception e) {
            System.err.println("❌ Client Error: Could not connect to the server or during communication.");
            System.err.println(e.getMessage());
        }
    }

    private void runTestMenu(ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) throws Exception {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Lecturer Client Test Menu ---");
            System.out.println("1. LOGIN");
            System.out.println("2. ADD_RESULT_MANUAL");
            System.out.println("3. VIEW_RESULTS_BY_MODULE");
            System.out.println("4. UPLOAD_CSV");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            Response response = null;

            switch (choice) {
                case "1":
                    response = testLogin(oos, ois, scanner);
                    break;
                case "2":
                    response = testAddResult(oos, ois, scanner);
                    break;
                case "3":
                    response = testViewResults(oos, ois, scanner);
                    break;
                case "4":
                    response = testCsvUpload(oos, ois, scanner); // New Test Method
                    break;
                case "5":
                    running = false;
                    System.out.println("Disconnecting from server...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

            if (response != null) {
                System.out.println("--- Server Response ---");
                System.out.println("STATUS: " + response.getStatus());
                System.out.println("MESSAGE: " + response.getMessage());
                if (response.getData() != null) {
                    System.out.println("DATA TYPE: " + response.getData().getClass().getSimpleName());
                    System.out.println("DATA: " + response.getData());
                }
                System.out.println("-------------------------");
            }
        }
    }

    // --- TEST HANDLERS ---

    private Response testLogin(ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) throws Exception {
        System.out.print("Enter Lecturer ID (e.g., L001): ");
        String id = scanner.nextLine();
        System.out.print("Enter Password (e.g., pass123): ");
        String password = scanner.nextLine();

        // FIX: The constructor requires a third argument: the role string.
        final String ROLE = "LECTURER";

        LoginRequest loginData = new LoginRequest(id, password, ROLE); // <-- FIXED: Added ROLE
        // Request type is "LOGIN"
        Request request = new Request("LOGIN", loginData);

        oos.writeObject(request);
        oos.flush();
        return (Response) ois.readObject();
    }

    private Response testAddResult(ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) throws Exception {
        // NOTE: Fill this in with realistic data for a successful test
        System.out.print("Enter Student ID (e.g., S001): ");
        String studentId = scanner.nextLine();
        System.out.print("Enter Module Code (e.g., CS101): ");
        String moduleCode = scanner.nextLine();
        System.out.print("Enter Marks (0-100): ");
        double marks = Double.parseDouble(scanner.nextLine());

        // Create a minimal Result object (assuming the model has these setters)
        Result resultData = new Result();
        resultData.setStudentId(studentId);
        resultData.setModuleCode(moduleCode);
        resultData.setMarks((int) marks);
        // Exam ID is often mandatory, but let's assume it's optional for now or is part of Result's constructor.
        // If mandatory, ensure you set it: resultData.setExamId("E001");

        // Request type is "ADD_RESULT_MANUAL"
        Request request = new Request("ADD_RESULT_MANUAL", resultData);

        oos.writeObject(request);
        oos.flush();
        return (Response) ois.readObject();
    }

    private Response testViewResults(ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) throws Exception {
        System.out.print("Enter Module Code to View (e.g., CS101): ");
        String moduleCode = scanner.nextLine();

        // Request type is "VIEW_RESULTS_BY_MODULE"
        Request request = new Request("VIEW_RESULTS_BY_MODULE", moduleCode);

        oos.writeObject(request);
        oos.flush();
        return (Response) ois.readObject();
    }

    private Response testCsvUpload(ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) throws Exception {
        String csvContent =
                "Student ID,Module Code,Marks Obtained\n" +
                        "S001,CS101,85\n" +
                        "S002,CS101,10\n" +
                        "S999,CS101,70\n" + // Invalid Student
                        "S001,MX999,95\n" + // Invalid Module
                        "S003,CS101,105\n" + // Invalid Marks Range
                        "S004,CS101,abc"; // Invalid Marks Format

        // Request type is UPLOAD_CSV
        Request request = new Request(common.utils.Constants.UPLOAD_CSV, csvContent);

        System.out.println("Sending CSV data to server...");
        oos.writeObject(request);
        oos.flush();
        return (Response) ois.readObject();
    }
}
