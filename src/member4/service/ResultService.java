package member4.service;

import common.models.Result;
import common.network.Response;
import common.storage.FileManager;
import common.utils.Constants;
import common.utils.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class ResultService {

    private final FileManager fileManager = FileManager.getInstance();
    private final GradeCalculator gradeCalculator = new GradeCalculator();

    // We assume Constants has: public static final String RESULTS_FILE = "results.dat";

    /**
     * Creates and saves a new result (used for manual entry and bulk uploads).
     * Automatically assigns ID and calculates grade.
     */
    public Response addResult(Result result) {
        try {
            // 1. Assign a unique ID
            if (result.getId() == null || result.getId().isEmpty()) {
                result.setId(IDGenerator.generateResultId());
            }

            // 2. Calculate Grade
            String grade = gradeCalculator.calculateGrade(result.getMarks());
            result.setGrade(grade);

            // 3. Save to persistence layer (FileManager)
            fileManager.save(result, Constants.RESULTS_FILE); // Uses save(Object, filename)
            System.out.println("Result added: " + result.getId() + " (Marks: " + result.getMarks() + ", Grade: " + result.getGrade() + ")");

            return Response.success(result);
        } catch (Exception e) {
            System.err.println("Error adding result: " + e.getMessage());
            return Response.error("Failed to add result due to server error: " + e.getMessage());
        }
    }

    /**
     * Helper to load and cast all results from file.
     */
    @SuppressWarnings("unchecked")
    public List<Result> getAllResults() {
        // Uses loadAll(filename) and safely casts the generic list
        return fileManager.loadAll(Constants.RESULTS_FILE).stream()
                .filter(obj -> obj instanceof Result)
                .map(obj -> (Result) obj)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves results for a specific module or student (Core CRUD Read operation).
     * Implementation will be finalized in Task 1.5.
     */
    public Response viewResults(String identifier, String type) {
        List<Result> allResults = getAllResults();
        List<Result> filteredResults = null;

        // This logic handles the VIEW_RESULTS_BY_MODULE request.
        if (type.equalsIgnoreCase("MODULE")) {
            filteredResults = allResults.stream()
                    .filter(r -> r.getModuleCode().equalsIgnoreCase(identifier))
                    .collect(Collectors.toList());
        }
        // We can add logic for "STUDENT" later here:
        /*
        else if (type.equalsIgnoreCase("STUDENT")) {
            filteredResults = allResults.stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(identifier))
                .collect(Collectors.toList());
        }
        */
        if (filteredResults == null || filteredResults.isEmpty()) {
            return Response.failure("No results found for " + type + " code: " + identifier);
        }

        return Response.success(filteredResults);
    }

    // Placeholder for Task 2.5: For bulk saving in CSVProcessor
    public void saveAllResults(List<Result> results) {
        // Implementation will be complex due to fileManager's save logic.
        // The most robust way is to load all, update the list, and use saveAll.

        List<Result> currentResults = getAllResults();
        currentResults.addAll(results); // Add new results

        fileManager.saveAll(currentResults, Constants.RESULTS_FILE);
        System.out.println("Bulk saved " + results.size() + " new results.");
    }
}