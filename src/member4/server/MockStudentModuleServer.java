package member4.server;

import common.models.Student;
import common.models.Module;
import common.network.Request;
import common.network.Response;
import common.utils.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MockStudentModuleServer {

    private static final int PORT = Constants.STUDENT_MODULE_PORT; // 8002

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🟢 MOCK Member 2 Server running on port " + PORT + " (Simulating Student & Module data).");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new MockHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("❌ MOCK Server failed to start: " + e.getMessage());
        }
    }

    private static class MockHandler extends Thread {
        private final Socket clientSocket;

        public MockHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())
            ) {
                Request request = (Request) ois.readObject();
                Response response = processMockRequest(request);
                oos.writeObject(response);
                oos.flush();
            } catch (Exception e) {
                // Ignore silent connection drops
            }
        }

        // Simulates the behavior of Member 2 Server
        private Response processMockRequest(Request request) {
            String identifier = (String) request.getData();

            switch (request.getType()) {

                case Constants.VIEW_STUDENT:
                    // Only Student S001 is VALID (matching your test data)
                    if ("S001".equalsIgnoreCase(identifier)) {
                        System.out.println("   [MOCK]: Student S001 found (SUCCESS).");
                        // Return SUCCESS with a minimal Student object
                        return Response.success(new Student());
                    } else {
                        System.out.println("   [MOCK]: Student " + identifier + " not found (FAILURE).");
                        return Response.failure("Mock: Student not found.");
                    }

                case Constants.VIEW_MODULE:
                    // Only Module CS101 is VALID (matching your test data)
                    if ("CS101".equalsIgnoreCase(identifier)) {
                        System.out.println("   [MOCK]: Module CS101 found (SUCCESS).");
                        // Return SUCCESS with a minimal Module object
                        return Response.success(new Module("CS101", "Mock Module", 3)); // Use the explicit constructor
                    } else {
                        System.out.println("   [MOCK]: Module " + identifier + " not found (FAILURE).");
                        return Response.failure("Mock: Module not found.");
                    }

                default:
                    return Response.error("Mock: Unknown request type received.");
            }
        }
    }
}
