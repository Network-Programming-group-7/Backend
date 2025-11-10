package src;

import common.models.Exam;
import common.network.ExamRequest;
import common.network.ExamResponse;
import common.storage.ExamStorage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExamClientHandler implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final ExamStorage storage;
    
    public ExamClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.storage = ExamStorage.getInstance();
    }
    
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            System.out.println("[" + Thread.currentThread().getName() + "] Client connected: " + 
                             clientSocket.getInetAddress().getHostAddress());
            
            while (!clientSocket.isClosed()) {
                try {
                    ExamRequest request = (ExamRequest) in.readObject();
                    System.out.println("[" + Thread.currentThread().getName() + "] Processing: " + request.getAction());
                    
                    ExamResponse response = processRequest(request);
                    out.writeObject(response);
                    out.flush();
                    
                } catch (ClassNotFoundException e) {
                    System.err.println("Invalid request class: " + e.getMessage());
                    ExamResponse errorResponse = new ExamResponse(false, "Invalid request format");
                    out.writeObject(errorResponse);
                    out.flush();
                }
            }
            
        } catch (IOException e) {
            System.out.println("[" + Thread.currentThread().getName() + "] Client disconnected");
        } finally {
            closeConnection();
        }
    }
    
    private ExamResponse processRequest(ExamRequest request) {
        String action = request.getAction();
        
        switch (action) {
            case "ADD_EXAM":
                return addExam(request);
            case "GET_EXAM":
                return getExam(request);
            case "GET_ALL_EXAMS":
                return getAllExams();
            case "UPDATE_EXAM":
                return updateExam(request);
            case "DELETE_EXAM":
                return deleteExam(request);
            case "GET_EXAMS_BY_MODULE":
                return getExamsByModule(request);
            case "GET_EXAMS_BY_BATCH":
                return getExamsByBatch(request);
            case "CHECK_CONFLICT":
                return checkConflict(request);
            case "CHECK_VENUE_AVAILABILITY":
                return checkVenueAvailability(request);
            case "GET_UPCOMING_EXAMS":
                return getUpcomingExams();
            case "GET_PAST_EXAMS":
                return getPastExams();
            case "GET_CONFLICT_DETAILS":
                return getConflictDetails(request);
            case "GET_STATISTICS":
                return getStatistics();
            default:
                return new ExamResponse(false, "Unknown action: " + action);
        }
    }
    
    private ExamResponse addExam(ExamRequest request) {
        try {
            Exam exam = request.getExam();
            
            if (exam == null) {
                return new ExamResponse(false, "Exam data is required");
            }
            
            if (exam.getId() == null || exam.getId().trim().isEmpty()) {
                return new ExamResponse(false, "Exam ID is required");
            }
            
            if (exam.getModuleCode() == null || exam.getModuleCode().trim().isEmpty()) {
                return new ExamResponse(false, "Module code is required");
            }
            
            if (exam.getBatchId() == null || exam.getBatchId().trim().isEmpty()) {
                return new ExamResponse(false, "Batch ID is required");
            }
            
            if (exam.getDate() == null) {
                return new ExamResponse(false, "Exam date is required");
            }
            
            if (exam.getStartTime() == null || exam.getEndTime() == null) {
                return new ExamResponse(false, "Start time and end time are required");
            }
            
            if (!isValidTimeFormat(exam.getStartTime()) || !isValidTimeFormat(exam.getEndTime())) {
                return new ExamResponse(false, "Invalid time format. Use HH:mm");
            }
            
            if (compareTime(exam.getStartTime(), exam.getEndTime()) >= 0) {
                return new ExamResponse(false, "Start time must be before end time");
            }
            
            if (exam.getVenue() == null || exam.getVenue().trim().isEmpty()) {
                return new ExamResponse(false, "Venue is required");
            }
            
            if (exam.getMaxMarks() <= 0) {
                return new ExamResponse(false, "Max marks must be greater than 0");
            }
            
            if (exam.getType() == null || exam.getType().trim().isEmpty()) {
                return new ExamResponse(false, "Exam type is required");
            }
            
            String examType = exam.getType().toUpperCase();
            if (!examType.equals("MIDTERM") && !examType.equals("FINAL") && !examType.equals("PRACTICAL")) {
                return new ExamResponse(false, "Exam type must be MIDTERM, FINAL, or PRACTICAL");
            }
            
            boolean added = storage.addExam(exam);
            
            if (added) {
                System.out.println("[SUCCESS] Exam added: " + exam.getId());
                return new ExamResponse(true, "Exam added successfully", exam);
            } else {
                if (storage.getExam(exam.getId()) != null) {
                    return new ExamResponse(false, "Exam with ID '" + exam.getId() + "' already exists");
                } else if (storage.hasConflict(exam)) {
                    List<String> conflicts = storage.getConflictDetails(exam);
                    return new ExamResponse(false, "Exam conflicts with existing schedule: " + 
                                          String.join("; ", conflicts));
                } else {
                    return new ExamResponse(false, "Cannot schedule exam in the past");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error adding exam: " + e.getMessage());
            e.printStackTrace();
            return new ExamResponse(false, "Error adding exam: " + e.getMessage());
        }
    }
    
    private ExamResponse getExam(ExamRequest request) {
        try {
            String examId = request.getExamId();
            
            if (examId == null || examId.trim().isEmpty()) {
                return new ExamResponse(false, "Exam ID is required");
            }
            
            Exam exam = storage.getExam(examId);
            
            if (exam != null) {
                return new ExamResponse(true, "Exam retrieved successfully", exam);
            } else {
                return new ExamResponse(false, "Exam not found");
            }
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving exam: " + e.getMessage());
        }
    }
    
    private ExamResponse getAllExams() {
        try {
            List<Exam> exams = storage.getAllExams();
            return new ExamResponse(true, "Retrieved " + exams.size() + " exams", exams);
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving exams: " + e.getMessage());
        }
    }
    
    private ExamResponse updateExam(ExamRequest request) {
        try {
            Exam exam = request.getExam();
            
            if (exam == null) {
                return new ExamResponse(false, "Exam data is required");
            }
            
            if (exam.getId() == null || exam.getId().trim().isEmpty()) {
                return new ExamResponse(false, "Exam ID is required");
            }
            
            if (exam.getModuleCode() == null || exam.getModuleCode().trim().isEmpty()) {
                return new ExamResponse(false, "Module code is required");
            }
            
            if (exam.getBatchId() == null || exam.getBatchId().trim().isEmpty()) {
                return new ExamResponse(false, "Batch ID is required");
            }
            
            if (exam.getDate() == null) {
                return new ExamResponse(false, "Exam date is required");
            }
            
            if (exam.getStartTime() == null || exam.getEndTime() == null) {
                return new ExamResponse(false, "Start time and end time are required");
            }
            
            if (!isValidTimeFormat(exam.getStartTime()) || !isValidTimeFormat(exam.getEndTime())) {
                return new ExamResponse(false, "Invalid time format. Use HH:mm");
            }
            
            if (compareTime(exam.getStartTime(), exam.getEndTime()) >= 0) {
                return new ExamResponse(false, "Start time must be before end time");
            }
            
            if (exam.getVenue() == null || exam.getVenue().trim().isEmpty()) {
                return new ExamResponse(false, "Venue is required");
            }
            
            if (exam.getMaxMarks() <= 0) {
                return new ExamResponse(false, "Max marks must be greater than 0");
            }
            
            if (exam.getType() == null || exam.getType().trim().isEmpty()) {
                return new ExamResponse(false, "Exam type is required");
            }
            
            String examType = exam.getType().toUpperCase();
            if (!examType.equals("MIDTERM") && !examType.equals("FINAL") && !examType.equals("PRACTICAL")) {
                return new ExamResponse(false, "Exam type must be MIDTERM, FINAL, or PRACTICAL");
            }
            
            boolean updated = storage.updateExam(exam);
            
            if (updated) {
                System.out.println("[SUCCESS] Exam updated: " + exam.getId());
                return new ExamResponse(true, "Exam updated successfully", exam);
            } else {
                if (storage.getExam(exam.getId()) == null) {
                    return new ExamResponse(false, "Exam not found");
                } else {
                    List<String> conflicts = storage.getConflictDetails(exam);
                    if (!conflicts.isEmpty()) {
                        return new ExamResponse(false, "Update failed - conflicts: " + 
                                              String.join("; ", conflicts));
                    } else {
                        return new ExamResponse(false, "Cannot schedule exam in the past");
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error updating exam: " + e.getMessage());
            return new ExamResponse(false, "Error updating exam: " + e.getMessage());
        }
    }
    
    private ExamResponse deleteExam(ExamRequest request) {
        try {
            String examId = request.getExamId();
            
            if (examId == null || examId.trim().isEmpty()) {
                return new ExamResponse(false, "Exam ID is required");
            }
            
            boolean deleted = storage.deleteExam(examId);
            
            if (deleted) {
                System.out.println("[SUCCESS] Exam deleted: " + examId);
                return new ExamResponse(true, "Exam deleted successfully");
            } else {
                return new ExamResponse(false, "Exam not found");
            }
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error deleting exam: " + e.getMessage());
        }
    }
    
    private ExamResponse getExamsByModule(ExamRequest request) {
        try {
            String moduleCode = request.getModuleCode();
            
            if (moduleCode == null || moduleCode.trim().isEmpty()) {
                return new ExamResponse(false, "Module code is required");
            }
            
            List<Exam> exams = storage.getExamsByModule(moduleCode);
            return new ExamResponse(true, "Retrieved " + exams.size() + " exams for module " + moduleCode, exams);
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving exams: " + e.getMessage());
        }
    }
    
    private ExamResponse getExamsByBatch(ExamRequest request) {
        try {
            String batchId = request.getBatchId();
            
            if (batchId == null || batchId.trim().isEmpty()) {
                return new ExamResponse(false, "Batch ID is required");
            }
            
            List<Exam> exams = storage.getExamsByBatch(batchId);
            return new ExamResponse(true, "Retrieved exam schedule for batch " + batchId + 
                                  " (" + exams.size() + " exams)", exams);
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving exam schedule: " + e.getMessage());
        }
    }
    
    private ExamResponse checkConflict(ExamRequest request) {
        try {
            Exam exam = request.getExam();
            
            if (exam == null) {
                return new ExamResponse(false, "Exam data is required");
            }
            
            boolean hasConflict = storage.hasConflict(exam);
            
            if (hasConflict) {
                List<String> conflicts = storage.getConflictDetails(exam);
                ExamResponse response = new ExamResponse(false, "Conflict detected");
                response.setConflicts(conflicts);
                return response;
            } else {
                return new ExamResponse(true, "No conflict detected - exam can be scheduled");
            }
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error checking conflict: " + e.getMessage());
        }
    }
    
    private ExamResponse checkVenueAvailability(ExamRequest request) {
        try {
            String venue = request.getVenue();
            
            if (venue == null || venue.trim().isEmpty()) {
                return new ExamResponse(false, "Venue is required");
            }
            
            if (request.getDate() == null) {
                return new ExamResponse(false, "Date is required");
            }
            
            if (request.getStartTime() == null || request.getEndTime() == null) {
                return new ExamResponse(false, "Start time and end time are required");
            }
            
            boolean available = storage.isVenueAvailable(venue, request.getDate(), 
                                                         request.getStartTime(), request.getEndTime());
            
            if (available) {
                return new ExamResponse(true, "Venue '" + venue + "' is available for the specified time slot");
            } else {
                return new ExamResponse(false, "Venue '" + venue + "' is not available for the specified time slot");
            }
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error checking venue availability: " + e.getMessage());
        }
    }
    
    private ExamResponse getUpcomingExams() {
        try {
            List<Exam> exams = storage.getUpcomingExams();
            return new ExamResponse(true, "Retrieved " + exams.size() + " upcoming exams", exams);
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving upcoming exams: " + e.getMessage());
        }
    }
    
    private ExamResponse getPastExams() {
        try {
            List<Exam> exams = storage.getPastExams();
            return new ExamResponse(true, "Retrieved " + exams.size() + " past exams", exams);
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving past exams: " + e.getMessage());
        }
    }
    
    private ExamResponse getConflictDetails(ExamRequest request) {
        try {
            Exam exam = request.getExam();
            
            if (exam == null) {
                return new ExamResponse(false, "Exam data is required");
            }
            
            List<String> conflicts = storage.getConflictDetails(exam);
            
            ExamResponse response = new ExamResponse(conflicts.isEmpty(), 
                conflicts.isEmpty() ? "No conflicts found" : "Found " + conflicts.size() + " conflicts");
            response.setConflicts(conflicts);
            return response;
            
        } catch (Exception e) {
            return new ExamResponse(false, "Error getting conflict details: " + e.getMessage());
        }
    }
    
    private ExamResponse getStatistics() {
        try {
            Map<String, Integer> stats = storage.getStatistics();
            ExamResponse response = new ExamResponse(true, "Statistics retrieved successfully");
            response.setStatistics(stats);
            return response;
        } catch (Exception e) {
            return new ExamResponse(false, "Error retrieving statistics: " + e.getMessage());
        }
    }
    
    private boolean isValidTimeFormat(String time) {
        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60;
    }
    
    private int compareTime(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int minutes1 = Integer.parseInt(parts1[0]) * 60 + Integer.parseInt(parts1[1]);
        int minutes2 = Integer.parseInt(parts2[0]) * 60 + Integer.parseInt(parts2[1]);
        return minutes1 - minutes2;
    }
    
    private void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("[" + Thread.currentThread().getName() + "] Connection closed");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}