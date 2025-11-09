package common.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import common.storage.DataStore;

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