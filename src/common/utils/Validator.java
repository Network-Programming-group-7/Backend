package common.utils;

import java.util.Date;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10}$"
    );
    
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidMarks(int marks) {
        return marks >= Constants.MIN_MARKS && marks <= Constants.MAX_MARKS;
    }
    
    public static boolean isValidDate(Date date) {
        if (date == null) return false;
        return !date.before(new Date());
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 6;
    }
}