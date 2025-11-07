package member4.service;

import common.models.Lecturer;
import common.network.Response;
import common.network.Request;
import common.network.LoginRequest;
import common.storage.DataStorage;
import common.storage.FileManager;
import common.utils.Constants;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationService {

    private final FileManager fileManager = FileManager.getInstance();

    /**
     * Entry point for the LOGIN operation from the ResultHandler.
     */
    public Response handleLogin(Request request) {
        if (!(request.getData() instanceof LoginRequest)) {
            return Response.error("Invalid login data format.");
        }
        LoginRequest loginRequest = (LoginRequest) request.getData();
        return authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * Core logic to verify Lecturer ID and password.
     */
    private Response authenticate(String lecturerId, String password) {

        // FIX 2: Correctly load all lecturers using FileManager's loadAll method
        List<Object> allObjects = fileManager.loadAll(Constants.LECTURERS_FILE);

        // Filter and safely cast the objects to Lecturer
        List<Lecturer> lecturers = allObjects.stream()
                .filter(obj -> obj instanceof Lecturer)
                .map(obj -> (Lecturer) obj)
                .collect(Collectors.toList());

        Lecturer lecturer = lecturers.stream()
                // FIX 3: getId() call is now assumed to exist in Lecturer.java
                .filter(l -> l.getId().equalsIgnoreCase(lecturerId))
                .findFirst()
                .orElse(null);

        if (lecturer == null) {
            return Response.failure("Login Failed: Invalid Lecturer ID.");
        }

        // FIX 3: getPassword() call is now assumed to exist in Lecturer.java
        if (lecturer.getPassword().equals(password)) {
            System.out.println("Auth success for Lecturer ID: " + lecturerId);
            return Response.success(lecturer);
        } else {
            return Response.failure("Login Failed: Invalid password.");
        }
    }
}