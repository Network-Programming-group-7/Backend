package member1.server;

import common.network.*;
import member1.service.*;
import java.net.*;
import java.io.*;

public class AdminHandler implements Runnable {
    private Socket socket;
    private AuthenticationService authService = new AuthenticationService();
    private LecturerService lecturerService = new LecturerService();
    private BatchService batchService = new BatchService(); // ✅ added service

    public AdminHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                Request req = (Request) in.readObject();
                Response res;

                switch (req.getType()) {
                    case "AUTH":
                        res = authService.handleAuth(req);
                        break;

                    // ✅ Lecturer management
                    case "CREATE_LECTURER":
                    case "LIST_LECTURERS":
                    case "VIEW_LECTURER":
                    case "UPDATE_LECTURER":
                    case "DELETE_LECTURER":
                        res = lecturerService.handle(req);
                        break;

                    // ✅ Batch management
                    case "CREATE_BATCH":
                    case "LIST_BATCHES":
                    case "VIEW_BATCH":
                    case "UPDATE_BATCH":
                    case "DELETE_BATCH":
                        res = batchService.handle(req);
                        break;

                    default:
                        res = Response.error("Unknown request type: " + req.getType());
                        break;
                }

                out.writeObject(res);
                out.flush();
            }

        } catch (EOFException eof) {
            System.out.println("👋 Admin client disconnected.");
        } catch (Exception e) {
            System.out.println("❌ Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
