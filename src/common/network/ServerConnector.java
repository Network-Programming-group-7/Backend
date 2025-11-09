package common.network;

import java.io.*;
import java.net.Socket;

public class ServerConnector {

    public static Response sendRequest(String host, int port, Request req) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(req);
            out.flush();

            return (Response) in.readObject();

        } catch (Exception e) {
            return Response.error("Failed to connect to server on port " + port + ": " + e.getMessage());
        }
    }
}
