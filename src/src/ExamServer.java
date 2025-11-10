package src;

import common.storage.ExamStorage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExamServer {
    private static final int PORT = 8003;
    private static final int THREAD_POOL_SIZE = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;
    
    public ExamServer() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.running = false;
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Exam Management Server started on port " + PORT);
            
            ExamStorage.getInstance();
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    
                    ExamClientHandler handler = new ExamClientHandler(clientSocket);
                    threadPool.execute(handler);
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Could not start server on port " + PORT + ": " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    public void stop() {
        running = false;
        
        if (threadPool != null) {
            threadPool.shutdown();
        }
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Exam Management Server stopped");
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        ExamServer server = new ExamServer();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down Exam Management Server...");
            server.stop();
        }));
        
        server.start();
    }
}