package member1.client;

import common.models.*;
import common.network.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class AdminClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8001;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            // ✅ Step 1 – Authentication
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            LoginRequest login = new LoginRequest(username, password, "Admin");
            Request loginReq = new Request("AUTH", login);
            out.writeObject(loginReq);
            out.flush();

            Response loginRes = (Response) in.readObject();
            System.out.println("Server → " + loginRes.getMessage());

            if (!"SUCCESS".equalsIgnoreCase(loginRes.getStatus())) {
                System.out.println("❌ Authentication failed. Exiting...");
                return;
            }

            // ✅ Step 2 – Menu loop
            while (true) {
                System.out.println("\n===== ADMIN MENU =====");
                System.out.println("1. Add Lecturer");
                System.out.println("2. View Lecturer");
                System.out.println("3. List All Lecturers");
                System.out.println("4. Update Lecturer");
                System.out.println("5. Delete Lecturer");
                System.out.println("6. Add Batch");
                System.out.println("7. View Batch");
                System.out.println("8. List All Batches");
                System.out.println("9. Update Batch");
                System.out.println("10. Delete Batch");
                System.out.println("11. Exit");
                System.out.print("Enter your choice: ");

                String choice = sc.nextLine();

                switch (choice) {
                    // Lecturer management
                    case "1":
                        addLecturer(out, in, sc);
                        break;
                    case "2":
                        viewLecturer(out, in, sc);
                        break;
                    case "3":
                        listLecturers(out, in);
                        break;
                    case "4":
                        updateLecturer(out, in, sc);
                        break;
                    case "5":
                        deleteLecturer(out, in, sc);
                        break;

                    // Batch management
                    case "6":
                        addBatch(out, in, sc);
                        break;
                    case "7":
                        viewBatch(out, in, sc);
                        break;
                    case "8":
                        listBatches(out, in);
                        break;
                    case "9":
                        updateBatch(out, in, sc);
                        break;
                    case "10":
                        deleteBatch(out, in, sc);
                        break;

                    // Exit
                    case "11":
                        System.out.println("👋 Exiting Admin Client...");
                        return;

                    default:
                        System.out.println("⚠️ Invalid choice. Try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ------------------- Lecturer CRUD Methods -------------------

    private static void addLecturer(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Lecturer ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Department: ");
        String department = sc.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine();

        Lecturer lec = new Lecturer(id, name, email, department);
        lec.setPhoneNumber(phone);

        Request req = new Request("CREATE_LECTURER", lec);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }

    private static void viewLecturer(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Lecturer ID: ");
        String id = sc.nextLine();

        Request req = new Request("VIEW_LECTURER", id);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
            Lecturer lec = (Lecturer) res.getData();
            System.out.println("✅ Lecturer Details: " + lec);
        } else {
            System.out.println("❌ " + res.getMessage());
        }
    }

    private static void listLecturers(ObjectOutputStream out, ObjectInputStream in) throws Exception {
        Request req = new Request("LIST_LECTURERS", null);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
            List<?> list = (List<?>) res.getData();
            if (list.isEmpty()) {
                System.out.println("📭 No lecturers found.");
            } else {
                System.out.println("📋 Lecturer List:");
                list.forEach(System.out::println);
            }
        } else {
            System.out.println("❌ " + res.getMessage());
        }
    }

    private static void updateLecturer(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Lecturer ID to Update: ");
        String id = sc.nextLine();
        System.out.print("Enter New Name: ");
        String name = sc.nextLine();
        System.out.print("Enter New Email: ");
        String email = sc.nextLine();
        System.out.print("Enter New Department: ");
        String department = sc.nextLine();
        System.out.print("Enter New Phone Number: ");
        String phone = sc.nextLine();

        Lecturer updated = new Lecturer(id, name, email, department);
        updated.setPhoneNumber(phone);

        Request req = new Request("UPDATE_LECTURER", updated);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }

    private static void deleteLecturer(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Lecturer ID to Delete: ");
        String id = sc.nextLine();

        Request req = new Request("DELETE_LECTURER", id);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }

    // ------------------- Batch CRUD Methods -------------------

    private static void addBatch(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Batch ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Batch Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Year: ");
        String year = sc.nextLine();
        System.out.print("Enter Semester: ");
        String semester = sc.nextLine();

        // 🧩 Ask for student IDs (comma-separated)
        System.out.print("Enter Student IDs (comma-separated): ");
        String studentInput = sc.nextLine();
        List<String> studentIds = new ArrayList<>();
        if (!studentInput.trim().isEmpty()) {
            studentIds = Arrays.asList(studentInput.split(","));
        }

        Batch batch = new Batch(id, name, year, semester);
        batch.setStudentIds(studentIds);

        Request req = new Request("CREATE_BATCH", batch);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }

    private static void viewBatch(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Batch ID: ");
        String id = sc.nextLine();

        Request req = new Request("VIEW_BATCH", id);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
            Batch b = (Batch) res.getData();
            System.out.println("✅ Batch Details: " + b);
        } else {
            System.out.println("❌ " + res.getMessage());
        }
    }

    private static void listBatches(ObjectOutputStream out, ObjectInputStream in) throws Exception {
        Request req = new Request("LIST_BATCHES", null);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
            List<?> list = (List<?>) res.getData();
            if (list.isEmpty()) {
                System.out.println("📭 No batches found.");
            } else {
                System.out.println("📋 Batch List:");
                list.forEach(System.out::println);
            }
        } else {
            System.out.println("❌ " + res.getMessage());
        }
    }

    private static void updateBatch(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Batch ID to Update: ");
        String id = sc.nextLine();
        System.out.print("Enter New Batch Name: ");
        String name = sc.nextLine();
        System.out.print("Enter New Year: ");
        String year = sc.nextLine();
        System.out.print("Enter New Semester: ");
        String semester = sc.nextLine();

        // 🧩 Ask for updated student IDs (comma-separated)
        System.out.print("Enter Updated Student IDs (comma-separated): ");
        String studentInput = sc.nextLine();
        List<String> studentIds = new ArrayList<>();
        if (!studentInput.trim().isEmpty()) {
            studentIds = Arrays.asList(studentInput.split(","));
        }

        Batch updated = new Batch(id, name, year, semester);
        updated.setStudentIds(studentIds);

        Request req = new Request("UPDATE_BATCH", updated);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }

    private static void deleteBatch(ObjectOutputStream out, ObjectInputStream in, Scanner sc) throws Exception {
        System.out.print("Enter Batch ID to Delete: ");
        String id = sc.nextLine();

        Request req = new Request("DELETE_BATCH", id);
        out.writeObject(req);
        out.flush();

        Response res = (Response) in.readObject();
        System.out.println("Server → " + res.getMessage());
    }
}
