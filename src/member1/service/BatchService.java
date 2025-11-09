package member1.service;

import common.models.Batch;
import common.network.*;
import common.storage.*;
import java.util.*;

public class BatchService {
    private static final String FILE_PATH = "data/batches.dat";
    private final DataStorage storage = new FileDataStorage();

    // 🔗 Member 2 server details for inter-server validation
    private static final String MEMBER2_HOST = "localhost";
    private static final int MEMBER2_PORT = 8002;

    private List<Batch> batches;

    public BatchService() {
        loadBatches();
    }

    // -------------------- Core file handling --------------------
    private void loadBatches() {
        Object data = storage.load(FILE_PATH);
        if (data instanceof List<?>) {
            batches = (List<Batch>) data;
        } else {
            batches = new ArrayList<>();
        }
    }

    private void saveBatches() {
        storage.save(batches, FILE_PATH);
    }

    // -------------------- Request Dispatcher --------------------
    public synchronized Response handle(Request req) {
        String type = req.getType();
        Object data = req.getData();

        switch (type) {
            case "CREATE_BATCH":
                return createBatch((Batch) data);
            case "LIST_BATCHES":
                loadBatches();
                return Response.success(batches);
            case "VIEW_BATCH":
                loadBatches();
                return viewBatch((String) data);
            case "UPDATE_BATCH":
                return updateBatch((Batch) data);
            case "DELETE_BATCH":
                return deleteBatch((String) data);
            default:
                return Response.error("Unknown request type: " + type);
        }
    }

    // -------------------- CRUD Logic --------------------

    // ✅ Create new batch and validate student IDs with Member 2
    private Response createBatch(Batch batch) {
        loadBatches();
        for (Batch b : batches) {
            if (b.getId().equalsIgnoreCase(batch.getId())) {
                return Response.failure("Batch ID already exists");
            }
        }

        // Validate each student ID through Member 2’s server
        if (batch.getStudentIds() != null && !batch.getStudentIds().isEmpty()) {
            for (String studentId : batch.getStudentIds()) {
                System.out.println("🔗 Validating student ID: " + studentId + " via Member 2 server...");
                Response validation = validateStudent(studentId);

                if (validation == null) {
                    System.out.println("⚠️ No response from Member 2 server.");
                    return Response.failure("Could not connect to Member 2 server.");
                }

                if (!"SUCCESS".equalsIgnoreCase(validation.getStatus())) {
                    System.out.println("❌ Invalid student ID: " + studentId);
                    return Response.failure("Invalid student ID: " + studentId);
                }

                System.out.println("✅ Student ID valid: " + studentId);
            }
        } else {
            System.out.println("ℹ️ No student IDs provided for this batch.");
        }

        batches.add(batch);
        saveBatches();
        return Response.success("Batch added successfully");
    }

    private Response viewBatch(String id) {
        for (Batch b : batches) {
            if (b.getId().equalsIgnoreCase(id)) {
                return Response.success(b);
            }
        }
        return Response.failure("Batch not found");
    }

    private Response updateBatch(Batch updated) {
        loadBatches();
        boolean found = false;

        for (int i = 0; i < batches.size(); i++) {
            if (batches.get(i).getId().equalsIgnoreCase(updated.getId())) {

                // Validate student IDs again before updating
                if (updated.getStudentIds() != null && !updated.getStudentIds().isEmpty()) {
                    for (String studentId : updated.getStudentIds()) {
                        System.out.println("🔁 Re-validating student ID: " + studentId + " via Member 2 server...");
                        Response validation = validateStudent(studentId);

                        if (validation == null) {
                            System.out.println("⚠️ No response from Member 2 server.");
                            return Response.failure("Could not connect to Member 2 server.");
                        }

                        if (!"SUCCESS".equalsIgnoreCase(validation.getStatus())) {
                            System.out.println("❌ Invalid student ID: " + studentId);
                            return Response.failure("Invalid student ID: " + studentId);
                        }

                        System.out.println("✅ Student ID valid: " + studentId);
                    }
                }

                batches.set(i, updated);
                found = true;
                break;
            }
        }

        if (found) {
            saveBatches();
            return Response.success("Batch updated successfully");
        }
        return Response.failure("Batch not found");
    }

    private Response deleteBatch(String id) {
        loadBatches();
        boolean removed = batches.removeIf(b -> b.getId().equalsIgnoreCase(id));
        if (removed) {
            saveBatches();
            return Response.success("Batch deleted successfully");
        }
        return Response.failure("Batch not found");
    }

    // -------------------- Inter-Server Communication --------------------

    /**
     * Sends a VALIDATE_STUDENT request to Member 2 server (port 8002)
     */
    private Response validateStudent(String studentId) {
        Request req = new Request("VALIDATE_STUDENT", studentId);
        Response res = ServerConnector.sendRequest(MEMBER2_HOST, MEMBER2_PORT, req);
        return res;
    }
}
