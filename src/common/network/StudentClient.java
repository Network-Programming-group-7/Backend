package network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class StudentClient {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("127.0.0.1", 8002)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            // Handshake
            out.write("ROLE|STUDENT\n"); out.flush();
            System.out.println(in.readLine()); // welcome
            Scanner sc = new Scanner(System.in);
            System.out.println("Type student commands, e.g., REGISTER|S1|CS101|1");
            while (true) {
                String line = sc.nextLine();
                out.write(line + "\n"); out.flush();
                String resp = in.readLine();
                if (resp == null) break;
                System.out.println(resp);
            }
        }
    }
}