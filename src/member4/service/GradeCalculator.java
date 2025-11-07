package member4.service;

import common.utils.Constants;

public class GradeCalculator {
    /**
     * Calculates a simple grade based on marks.
     */
    public String calculateGrade(double marks) {

        // Ensure marks are within the valid range before calculation
        if (marks > Constants.MAX_MARKS || marks < Constants.MIN_MARKS) {
            // This case should ideally be caught by ValidationService,
            // but the calculator must be robust.
            return "INVALID";
        }

        if (marks >= 85) {
            return "A+";
        } else if (marks >= 80) {
            return "A";
        } else if (marks >= 75) {
            return "A-";
        } else if (marks >= 70) {
            return "B+";
        } else if (marks >= 65) {
            return "B";
        } else if (marks >= 60) {
            return "B-";
        } else if (marks >= 55) {
            return "C+";
        } else if (marks >= 50) {
            return "C"; // C is specified in requirements
        } else if (marks >= 45) {
            return "C-"; // C- is included based on standard distribution
        } else if (marks >= Constants.PASS_MARKS) { // Use the defined pass mark (40)
            return "D";
        } else {
            return "F"; // Anything below the pass mark is a fail
        }
    }
}
