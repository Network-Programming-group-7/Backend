package member4.service;

import common.models.Result;
import common.network.Response;
import common.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVProcessor {

    // Dependencies for later tasks
    private final ValidationService validationService = new ValidationService();
    private final ResultService resultService = new ResultService();
    private final GradeCalculator gradeCalculator = new GradeCalculator();

    /**
     * Entry point for the UPLOAD_CSV request. Receives and processes the CSV content.
     */
    public Response handleCsvUpload(Object data) {

        if (!(data instanceof String)) {
            return Response.error("Invalid data format for CSV upload. Expected CSV content as a String.");
        }

        String csvContent = (String) data;

        if (csvContent.trim().isEmpty()) {
            return Response.failure("CSV file is empty. Please upload a file with results.");
        }

        System.out.println("Received CSV content. Total lines (approx): " + csvContent.split("\n").length);

        // --- Core Task 2.4: Parsing and Validation ---
        Map<String, Object> validationMap = validateAndParseCsv(csvContent);

        @SuppressWarnings("unchecked")
        List<Result> validResults = (List<Result>) validationMap.get("validResults");
        @SuppressWarnings("unchecked")
        List<String> errorReport = (List<String>) validationMap.get("errorReport");

        // --- Error Reporting ---
        if (!errorReport.isEmpty()) {
            System.err.println("CSV upload completed with " + errorReport.size() + " errors.");

            // If no valid results were found, return a FAILURE status with the error report.
            if (validResults.isEmpty()) {
                return new Response(Constants.FAILED, "CSV upload failed entirely. Errors found:", errorReport);
            }

            // If some results are valid, proceed to save them (Task 2.5), but report the errors.
            // Task 2.5: Bulk Saving
            resultService.saveAllResults(validResults);

            // Return success but include a warning message and the full error report.
            String successMsg = String.format("Upload partial success. Saved %d results. %d rows skipped due to errors.",
                    validResults.size(), errorReport.size());
            return new Response(Constants.SUCCESS, successMsg, errorReport);
        }

        // --- Task 2.5: Bulk Saving (100% Valid) ---
        resultService.saveAllResults(validResults);

        return Response.success("CSV content received successfully. Ready for processing (Parsing & Validation logic pending).");
    }

    /**
     * Parses CSV lines, creates Result objects, and validates each one.
     * Generates a list of valid results and a report of all errors (Task 2.4).
     */
    private Map<String, Object> validateAndParseCsv(String csvContent) {

        List<Result> validResults = new ArrayList<>();
        List<String> errorReport = new ArrayList<>();

        // Split content into lines, skipping the header (assumed to be line 1)
        String[] lines = csvContent.split("\\r?\\n");

        // Start from line 2 (index 1) assuming row 1 is a header
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] fields = line.split(",");
            int lineNumber = i + 1;

            // 1. Basic Structure Check
            if (fields.length != 3) {
                errorReport.add("Line " + lineNumber + ": Incorrect number of fields. Expected 3 (Student ID, Module Code, Marks).");
                continue;
            }

            // Extract fields
            String studentId = fields[0].trim();
            String moduleCode = fields[1].trim();
            double marks = -1; // Default to invalid marks
            String marksString = fields[2].trim();

            // 2. Marks Format Check (Attempt to parse double)
            try {
                marks = Double.parseDouble(marksString);
            } catch (NumberFormatException e) {
                errorReport.add("Line " + lineNumber + ": Invalid marks format: '" + marksString + "'. Must be a number.");
                continue;
            }

            // 3. Create Result Object and Perform Comprehensive Validation (Task 2.2)
            try {
                // We use the setters to populate the object
                Result result = new Result();
                result.setStudentId(studentId);
                result.setModuleCode(moduleCode);
                result.setMarks((int) marks);

                // Call the full validation service (which talks to Member 2)
                Response validationResponse = validationService.validateResult(result);

                if (validationResponse.getStatus().equals(Constants.SUCCESS)) {
                    validResults.add(result);
                } else {
                    // Validation failed (e.g., marks out of range, student ID not found)
                    errorReport.add("Line " + lineNumber + ": Validation failed. Reason: " + validationResponse.getMessage());
                }
            } catch (Exception e) {
                errorReport.add("Line " + lineNumber + ": Unexpected server error during result creation. " + e.getMessage());
            }
        }

        Map<String, Object> results = new HashMap<>();
        results.put("validResults", validResults);
        results.put("errorReport", errorReport);
        return results;
    }
}
