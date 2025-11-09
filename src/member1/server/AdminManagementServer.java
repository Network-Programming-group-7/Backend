package member1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminManagementServer {
    public static final int PORT = 8001;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Admin Management Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("👤 Admin client connected: " + clientSocket.getInetAddress());
                new Thread(new AdminHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("❌ Server Error: " + e.getMessage());
        }
    }
}
