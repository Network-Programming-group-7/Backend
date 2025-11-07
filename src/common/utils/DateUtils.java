package common.utils;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    
    /**
     * Format date to yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Format date to yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATE_TIME_FORMAT.format(date);
    }
    
    /**
     * Format time to HH:mm
     */
    public static String formatTime(Date date) {
        if (date == null) return "";
        return TIME_FORMAT.format(date);
    }
    
    /**
     * Parse string to Date (yyyy-MM-dd)
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + dateStr);
            return null;
        }
    }
    
    /**
     * Parse string to Date (yyyy-MM-dd HH:mm:ss)
     */
    public static Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_TIME_FORMAT.parse(dateTimeStr);
        } catch (ParseException e) {
            System.err.println("Error parsing datetime: " + dateTimeStr);
            return null;
        }
    }
    
    /**
     * Check if date is in the past
     */
    public static boolean isInPast(Date date) {
        if (date == null) return false;
        return date.before(new Date());
    }
    
    /**
     * Check if date is today
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        String today = formatDate(new Date());
        String checkDate = formatDate(date);
        return today.equals(checkDate);
    }
    
    /**
     * Check if date is in the future
     */
    public static boolean isInFuture(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }
    
    /**
     * Get number of days between two dates
     */
    public static long getDaysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diff = end.getTime() - start.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
    
    /**
     * Add days to a date
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
    
    /**
     * Add months to a date
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }
    
    /**
     * Get current date and time
     */
    public static Date getCurrentDateTime() {
        return new Date();
    }
    
    /**
     * Get current date (time set to 00:00:00)
     */
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Compare two dates (ignoring time)
     */
    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        String d1 = formatDate(date1);
        String d2 = formatDate(date2);
        return d1.equals(d2);
    }
    
    /**
     * Get year from date
     */
    public static int getYear(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
    
    /**
     * Get month from date (1-12)
     */
    public static int getMonth(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
     * Get day of month from date
     */
    public static int getDayOfMonth(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}