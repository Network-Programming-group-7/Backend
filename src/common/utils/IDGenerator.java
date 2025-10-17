package common.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static AtomicInteger lecturerCounter = new AtomicInteger(1);
    private static AtomicInteger studentCounter = new AtomicInteger(1);
    private static AtomicInteger batchCounter = new AtomicInteger(1);
    private static AtomicInteger examCounter = new AtomicInteger(1);
    private static AtomicInteger resultCounter = new AtomicInteger(1);
    
    public static String generateLecturerId() {
        return String.format("L%03d", lecturerCounter.getAndIncrement());
    }
    
    public static String generateStudentId() {
        return String.format("S%03d", studentCounter.getAndIncrement());
    }
    
    public static String generateBatchId() {
        return String.format("B%03d", batchCounter.getAndIncrement());
    }
    
    public static String generateExamId() {
        return String.format("E%03d", examCounter.getAndIncrement());
    }
    
    public static String generateResultId() {
        return String.format("R%03d", resultCounter.getAndIncrement());
    }
}