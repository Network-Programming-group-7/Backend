package network;

import storage.DataStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer implements Runnable {
    private final int port;
    private final DataStore store;
    private volatile boolean running = true;

    public NetworkServer(int port, DataStore store) {
        this.port = port;
        this.store = store;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on " + port);
            while (running) {
                Socket client = server.accept();
                // Determine role from first line
                new Thread(() -> {
                    try (Socket s = client) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String roleLine = br.readLine();
                        if (roleLine == null) return;
                        String[] p = roleLine.split("\\|");
                        String role = p.length > 1 ? p[1].trim().toUpperCase() : "";
                        Socket pass = client; // pass same socket to handler; do not close here
                        // Re-open streams in handler; do not close here
                        Runnable handler;
                        if ("ADMIN".equals(role)) {
                            handler = new AdminClientHandler(pass, store);
                        } else if ("STUDENT".equals(role)) {
                            handler = new StudentClientHandler(pass, store);
                        } else {
                            // Unknown role; simple echo then close
                            pass.getOutputStream().write(("ERROR|UnknownRole\n").getBytes());
                            pass.close();
                            return;
                        }
                        new Thread(handler, "ClientHandler-" + role + "-" + pass.getPort()).start();
                    } catch (IOException ignored) {}
                }, "RoleRouter-" + client.getPort()).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
    }
}