package member2.server;

import common.network.*;
import java.io.*;
import java.net.*;

public class MockMember2Server {
    public static final int PORT = 8002;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Mock Member 2 Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("👤 Member 1 connected for validation.");

                try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    Request req = (Request) in.readObject();
                    if ("VALIDATE_STUDENT".equals(req.getType())) {
                        String studentId = (String) req.getData();

                        // Simple rule: accept ST001–ST005, reject others
                        if (studentId.matches("ST00[1-5]")) {
                            out.writeObject(Response.success("Student ID valid: " + studentId));
                            System.out.println("✅ Validated: " + studentId);
                        } else {
                            out.writeObject(Response.failure("Invalid student ID: " + studentId));
                            System.out.println("❌ Invalid: " + studentId);
                        }

                        out.flush();
                    }

                } catch (Exception e) {
                    System.out.println("⚠️ Error handling request: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Could not start Mock Member 2 Server: " + e.getMessage());
        }
    }
}

