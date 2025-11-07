package member4.service;

import common.network.Request;
import common.network.Response;
import common.utils.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Member2Client {
    private static final String HOST = Constants.SERVER_HOST; // "localhost"
    private static final int PORT = Constants.STUDENT_MODULE_PORT; // 8002

    /**
     * Sends a generic request to the Member 2 Server and returns the response.
     */
    public Response sendRequest(Request request) {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            oos.writeObject(request);
            oos.flush();
            return (Response) ois.readObject();

        } catch (Exception e) {
            System.err.println("❌ Inter-Server Communication Error with Member 2 (Port " + PORT + "): " + e.getMessage());
            return Response.error("Internal server error during data validation.");
        }
    }

    /**
     * Checks if a Student ID exists in the system (Member 2's data).
     */
    public boolean checkStudentExists(String studentId) {
        // We use the VIEW_STUDENT request type (from Member 2's constants)
        Request request = new Request(Constants.VIEW_STUDENT, studentId);
        Response response = sendRequest(request);

        // Success status (and non-null data) indicates the student was found.
        return response.getStatus().equals(Constants.SUCCESS) && response.getData() != null;
    }

    /**
     * Checks if a Module Code exists in the system (Member 2's data).
     * Also fetches module details like credits, which are needed later.
     */
    public boolean checkModuleExists(String moduleCode) {
        // We use the VIEW_MODULE request type (from Member 2's constants)
        Request request = new Request(Constants.VIEW_MODULE, moduleCode);
        Response response = sendRequest(request);

        // Success status (and non-null data) indicates the module was found.
        return response.getStatus().equals(Constants.SUCCESS) && response.getData() != null;
    }
}
