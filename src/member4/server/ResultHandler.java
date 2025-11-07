package member4.server;

import common.models.Result;
import common.network.Request;
import common.network.Response;
import member4.service.AuthenticationService; // Used in Task 1.3
import common.models.Lecturer; // Used in Task 1.3
import member4.service.CSVProcessor;
import member4.service.ResultService;
import member4.service.ValidationService;
import common.utils.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ResultHandler extends Thread {

    private final Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private final ResultService resultService = new ResultService();
    private final ValidationService validationService = new ValidationService();
    private final CSVProcessor csvProcessor = new CSVProcessor();

    // Task 1.3: Track session state
    private Lecturer loggedInLecturer = null;
    private final AuthenticationService authService = new AuthenticationService();

    public ResultHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // NOTE: Output stream MUST be created first for reliable TCP Object communication
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());

            handleClientRequests();

        } catch (IOException e) {
            System.err.println("Client disconnected or I/O error on ResultHandler: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleClientRequests() throws IOException {
        try {
            // Keep reading requests as long as the connection is active
            while (clientSocket.isConnected()) {
                Request request = (Request) inputStream.readObject();
                System.out.println("Request [" + request.getType() + "] received from " + clientSocket.getInetAddress().getHostAddress());

                // Process the request and get a response
                Response response = processRequest(request);

                // Send the Response object back (Serialization)
                outputStream.writeObject(response);
                outputStream.flush();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Deserialization Error: Object received is not a valid Request.");
        }
    }

    // --- Request Processing Logic (Completed in subsequent tasks) ---
    private Response processRequest(Request request) {

        // Special case: Allow LOGIN even if loggedInLecturer is null
        if (request.getType().equals("LOGIN")) {
            Response authResponse = authService.handleLogin(request);

            // If authentication succeeded, update the session state
            if (authResponse.getStatus().equals("SUCCESS") && authResponse.getData() instanceof Lecturer) {
                this.loggedInLecturer = (Lecturer) authResponse.getData();
                return Response.success("Login successful. Welcome, " + this.loggedInLecturer.getName() + "!");
            }
            return authResponse; // Return failure/error response
        }

        // Session check for all OTHER operations
        if (loggedInLecturer == null) {
            return Response.failure("Authentication Required. Please log in first.");
        }

        // This switch will grow with every task
        switch (request.getType()) {

            case Constants.UPLOAD_CSV:
                return handleCsvUpload(request);

            case Constants.ADD_RESULT:
                return handleAddResultManual(request);

            case Constants.VIEW_RESULT:
                return handleViewResultsByModule(request);

            case Constants.UPDATE_RESULT:
                return Response.failure("Not implemented yet: UPDATE_RESULT");

            case Constants.DELETE_RESULT:
                return Response.failure("Not implemented yet: DELETE_RESULT");

            default:
                return Response.error("Unknown operation: " + request.getType());
        }
    }

    // New: Handles manual result entry
    private Response handleAddResultManual(Request request) {
        if (!(request.getData() instanceof Result)) {
            return Response.error("Invalid data format for ADD_RESULT_MANUAL. Expected Result object.");
        }
        Result newResult = (Result) request.getData();

        // Step 1: Input Validation
        Response validationResponse = validationService.validateResult(newResult);
        if (!validationResponse.getStatus().equals("SUCCESS")) {
            return validationResponse;
        }

        // Step 2: Persistence (Add ID/Grade and Save)
        return resultService.addResult(newResult);
    }

    // New: Handles viewing results
    private Response handleViewResultsByModule(Request request) {
        if (!(request.getData() instanceof String)) {
            return Response.error("Invalid data format for VIEW_RESULTS_BY_MODULE. Expected Module Code (String).");
        }
        String moduleCode = (String) request.getData();

        // Uses the viewResultsByModule from ResultService
        return resultService.viewResults(moduleCode, "MODULE");
    }

    private Response handleCsvUpload(Request request) {
        // We only pass the raw data object to the service layer for processing
        return csvProcessor.handleCsvUpload(request.getData());
    }

    private void closeConnection() {
        // ... (standard close logic for streams and socket) ...
        try {
            if (clientSocket != null) clientSocket.close();
            System.out.println(" Connection closed for lecturer: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            System.err.println("Error closing connection.");
        }
    }
}