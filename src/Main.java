import network.NetworkServer;
import storage.DataStore;

public class Main {
    public static void main(String[] args) {
        DataStore store = new DataStore();
        NetworkServer server = new NetworkServer(8002, store);
        Thread t = new Thread(server, "TCP-Server-8002");
        t.start();
        // Server runs until process is terminated.
    }
}
