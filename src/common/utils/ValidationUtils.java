package common.utils;

import java.util.regex.Pattern;

import common.storage.DataStore;

public class ValidationUtils {
    private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_RE.matcher(email).matches();
    }

    // Placeholder for business rules: prerequisites/conflicts.
    public static boolean hasMetPrerequisites(DataStore store, String studentId, String moduleCode) {
        // Integrate real prerequisite checks here if needed.
        return true;
    }
}
