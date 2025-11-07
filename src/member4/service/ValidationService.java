package member4.service;

import common.models.Result;
import common.network.Response;
import common.utils.Constants;

public class ValidationService {

    private final Member2Client member2Client = new Member2Client();
    /**
     * Validates a single Result object before persistence.
     */
    public Response validateResult(Result result) {
        if (result == null) {
            return Response.failure("Result data is missing.");
        }

        // --- Validation Rule 1: Marks Range (Bounds Check) ---
        if (result.getMarks() < Constants.MIN_MARKS || result.getMarks() > Constants.MAX_MARKS) {
            return Response.failure("Invalid marks: Marks must be between " +
                    Constants.MIN_MARKS + " and " + Constants.MAX_MARKS + ".");
        }

        // --- Validation Rule 2: Student ID Existence (Inter-Server Check) ---
        if (!member2Client.checkStudentExists(result.getStudentId())) {
            return Response.failure("Invalid Student ID: Student " + result.getStudentId() + " not found in the system.");
        }

        // --- Validation Rule 3: Module Code Existence (Inter-Server Check) ---
        // Member 4 must validate module codes by communicating with Member 2 .
        if (!member2Client.checkModuleExists(result.getModuleCode())) {
            return Response.failure("Invalid Module Code: Module " + result.getModuleCode() + " not found in the system.");
        }

        // Future Rule: Check if the lecturer is authorized to submit results for this module
        // (Requires a check against Member 1's Lecturer data or Member 2's Module data)

        return Response.success("Result passed all validation checks.");
    }
}
