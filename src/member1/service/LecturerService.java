package member1.service;

import common.models.Lecturer;
import common.network.*;
import common.storage.*;
import java.util.*;

public class LecturerService {
    private static final String FILE_PATH = "data/lecturers.dat";
    private final DataStorage storage = new FileDataStorage();

    // Keep lecturers in memory but always sync to file
    private List<Lecturer> lecturers;

    public LecturerService() {
        loadLecturers();
    }

    // --- Helper to always reload latest list ---
    private void loadLecturers() {
        Object data = storage.load(FILE_PATH);
        if (data instanceof List<?>) {
            lecturers = (List<Lecturer>) data;
        } else {
            lecturers = new ArrayList<>();
        }
    }

    private void saveLecturers() {
        storage.save(lecturers, FILE_PATH);
    }

    // --- Main dispatcher ---
    public synchronized Response handle(Request req) {
        String type = req.getType();
        Object data = req.getData();

        switch (type) {
            case "CREATE_LECTURER":
                return createLecturer((Lecturer) data);
            case "LIST_LECTURERS":
                loadLecturers(); // ensure latest view
                return Response.success(lecturers);
            case "VIEW_LECTURER":
                loadLecturers();
                return viewLecturer((String) data);
            case "UPDATE_LECTURER":
                return updateLecturer((Lecturer) data);
            case "DELETE_LECTURER":
                return deleteLecturer((String) data);
            default:
                return Response.error("Unknown request type: " + type);
        }
    }

    // --- CRUD Operations ---
    private Response createLecturer(Lecturer lec) {
        loadLecturers();
        for (Lecturer l : lecturers)
            if (l.getId().equalsIgnoreCase(lec.getId()))
                return Response.failure("Lecturer ID already exists");

        lecturers.add(lec);
        saveLecturers();
        return Response.success("Lecturer added successfully");
    }

    private Response viewLecturer(String id) {
        for (Lecturer l : lecturers)
            if (l.getId().equalsIgnoreCase(id))
                return Response.success(l);
        return Response.failure("Lecturer not found");
    }

    private Response updateLecturer(Lecturer updated) {
        loadLecturers();
        boolean found = false;
        for (int i = 0; i < lecturers.size(); i++) {
            if (lecturers.get(i).getId().equalsIgnoreCase(updated.getId())) {
                lecturers.set(i, updated);
                found = true;
                break;
            }
        }
        if (found) {
            saveLecturers();
            return Response.success("Lecturer updated");
        }
        return Response.failure("Lecturer not found");
    }

    private Response deleteLecturer(String id) {
        loadLecturers();
        boolean removed = lecturers.removeIf(l -> l.getId().equalsIgnoreCase(id));
        if (removed) {
            saveLecturers();
            return Response.success("Lecturer deleted");
        }
        return Response.failure("Lecturer not found");
    }
}
