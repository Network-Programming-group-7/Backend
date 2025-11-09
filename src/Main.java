import common.network.NetworkServer;
import common.network.UniversityWebSocketServer;
import common.storage.DataStore;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize DataStore
            DataStore store = new DataStore();
            
            // Start TCP Server (Port 8002)
            System.out.println("🚀 Starting TCP Socket Server...");
            NetworkServer tcpServer = new NetworkServer(8002, store);
            Thread tcpThread = new Thread(tcpServer, "TCP-Server-8002");
            tcpThread.setDaemon(false);
            tcpThread.start();
            System.out.println("✅ TCP Server running on port 8002");
            
            // Start WebSocket Server (Port 8003) - Bridge for frontend
            System.out.println("🚀 Starting WebSocket Server...");
            UniversityWebSocketServer wsServer = new UniversityWebSocketServer(8003, store);
            wsServer.start();
            System.out.println("✅ WebSocket Server running on port 8003");
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎓 UNIVERSITY MANAGEMENT SYSTEM - SERVER READY");
            System.out.println("=".repeat(50));
            System.out.println("📡 TCP Socket:    localhost:8002");
            System.out.println("🌐 WebSocket:     ws://localhost:8003");
            System.out.println("=".repeat(50) + "\n");
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ Server startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
