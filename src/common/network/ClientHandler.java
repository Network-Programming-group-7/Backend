package network;

import storage.DataStore;

import java.io.*;
import java.net.Socket;

public abstract class ClientHandler implements Runnable {
    protected final Socket socket;
    protected final DataStore store;
    private BufferedReader in;
    private BufferedWriter out;

    protected ClientHandler(Socket socket, DataStore store) {
        this.socket = socket;
        this.store = store;
    }

    @Override public void run() {
        try (Socket s = socket) {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            writeLine("OK|WELCOME");
            String line;
            while ((line = in.readLine()) != null) {
                String response = handle(line.trim());
                writeLine(response == null ? "ERROR|Unknown" : response);
            }
        } catch (IOException ignored) {}
    }

    protected void writeLine(String msg) throws IOException {
        out.write(msg);
        out.write("\n");
        out.flush();
    }

    protected abstract String handle(String cmdLine);
}