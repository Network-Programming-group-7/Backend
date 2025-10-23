package member5.service;

import common.models.Result;
import common.utils.DateUtils;
import member5.service.GPACalculator.GPA;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

/**
 * Generate student transcript in text format
 */
public class TranscriptGenerator {
    
    private GPACalculator gpaCalculator;
    
    public TranscriptGenerator() {
        this.gpaCalculator = new GPACalculator();
    }
    
    /**
     * Generate transcript for a student
     * @param studentId The student ID
     * @param results List of results
     * @return Transcript as byte array
     */
    public byte[] generateTranscript(String studentId, List<Result> results) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        // Header
        writer.println("╔══════════════════════════════════════════════════════════╗");
        writer.println("║           UNIVERSITY ACADEMIC TRANSCRIPT                ║");
        writer.println("╚══════════════════════════════════════════════════════════╝");
        writer.println();
        
        // Student Info
        writer.println("Student ID:     " + studentId);
        writer.println("Generated on:   " + DateUtils.formatDateTime(new Date()));
        writer.println();
        
        writer.println("═══════════════════════════════════════════════════════════");
        writer.println("                    ACADEMIC RECORD                        ");
        writer.println("═══════════════════════════════════════════════════════════");
        writer.println();
        
        // Table Header
        writer.println(String.format("%-12s %-30s %-8s %-6s %-8s", 
            "MODULE", "NAME", "CREDITS", "MARKS", "GRADE"));
        writer.println("─".repeat(70));
        
        // Results
        for (Result result : results) {
            String moduleName = result.getModuleName() != null ? 
                result.getModuleName() : "N/A";
            
            if (moduleName.length() > 28) {
                moduleName = moduleName.substring(0, 25) + "...";
            }
            
            writer.println(String.format("%-12s %-30s %-8d %-6d %-8s",
                result.getModuleCode(),
                moduleName,
                result.getCredits(),
                result.getMarks(),
                result.getGrade()));
        }
        
        writer.println("─".repeat(70));
        writer.println();
        
        // GPA Calculation
        GPA gpa = gpaCalculator.calculateGPA(results);
        
        writer.println("SUMMARY:");
        writer.println(String.format("  Total Modules Completed:  %d", gpa.getModulesCompleted()));
        writer.println(String.format("  Total Credits Earned:     %d", gpa.getTotalCredits()));
        writer.println(String.format("  Cumulative GPA:           %.2f / 4.00", gpa.getValue()));
        writer.println();
        
        // Classification
        String classification = getClassification(gpa.getValue());
        writer.println(String.format("  Classification:           %s", classification));
        writer.println();
        
        writer.println("═══════════════════════════════════════════════════════════");
        writer.println();
        
        // Footer
        writer.println("Note: This is a computer-generated document.");
        writer.println("      No signature is required.");
        writer.println();
        writer.println("─".repeat(70));
        writer.println("              END OF TRANSCRIPT");
        writer.println("─".repeat(70));
        
        writer.flush();
        writer.close();
        
        System.out.println("📄 Transcript generated for student: " + studentId);
        
        return baos.toByteArray();
    }
    
    /**
     * Get classification based on GPA
     */
    private String getClassification(double gpa) {
        if (gpa >= 3.70) return "First Class Honours";
        else if (gpa >= 3.30) return "Second Class Honours (Upper Division)";
        else if (gpa >= 3.00) return "Second Class Honours (Lower Division)";
        else if (gpa >= 2.00) return "Pass";
        else return "Fail";
    }
    
    /**
     * Convert byte array to string for display
     */
    public String transcriptToString(byte[] transcript) {
        return new String(transcript);
    }
}