package member4.server;

import common.utils.Constants; // Assumed to contain RESULT_SERVER_PORT = 8004
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ResultManagementServer {

    private static final int PORT = Constants.RESULT_PORT;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Result Management Server is running on port " + PORT + ".");
            System.out.println("Waiting for lecturer connections...");

            while (true) {
                // Task 1.2 will complete this loop with multithreading
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n🔌 New lecturer connected from: " + clientSocket.getInetAddress().getHostAddress());


                new ResultHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Critical Server Error: Could not listen on port " + PORT);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // The main method to start the server
        new ResultManagementServer().startServer();
    }
}