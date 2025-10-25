package common.storage;

import common.models.Exam;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExamStorage {
    private static ExamStorage instance;
    private final Map<String, Exam> exams;
    private final Map<String, List<String>> moduleExamMap;
    private final Map<String, List<String>> batchExamMap;
    private static final String DATA_FILE = "data/exams.dat";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private ExamStorage() {
        this.exams = new ConcurrentHashMap<>();
        this.moduleExamMap = new ConcurrentHashMap<>();
        this.batchExamMap = new ConcurrentHashMap<>();
        loadData();
    }
    
    public static synchronized ExamStorage getInstance() {
        if (instance == null) {
            instance = new ExamStorage();
        }
        return instance;
    }
    
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, Exam> loadedExams = (Map<String, Exam>) ois.readObject();
            exams.putAll(loadedExams);
            
            for (Exam exam : exams.values()) {
                moduleExamMap.putIfAbsent(exam.getModuleCode(), new ArrayList<>());
                moduleExamMap.get(exam.getModuleCode()).add(exam.getId());
                
                batchExamMap.putIfAbsent(exam.getBatchId(), new ArrayList<>());
                batchExamMap.get(exam.getBatchId()).add(exam.getId());
            }
            
            System.out.println("Loaded " + exams.size() + " exams from storage");
        } catch (Exception e) {
            System.err.println("Error loading exam data: " + e.getMessage());
        }
    }
    
    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(new HashMap<>(exams));
            }
        } catch (Exception e) {
            System.err.println("Error saving exam data: " + e.getMessage());
        }
    }
    
    public synchronized boolean addExam(Exam exam) {
        if (exams.containsKey(exam.getId())) {
            return false;
        }
        
        if (exam.getDate().before(new Date())) {
            return false;
        }
        
        if (hasConflict(exam)) {
            return false;
        }
        
        exams.put(exam.getId(), exam);
        
        moduleExamMap.putIfAbsent(exam.getModuleCode(), new ArrayList<>());
        moduleExamMap.get(exam.getModuleCode()).add(exam.getId());
        
        batchExamMap.putIfAbsent(exam.getBatchId(), new ArrayList<>());
        batchExamMap.get(exam.getBatchId()).add(exam.getId());
        
        saveData();
        return true;
    }
    
    public synchronized Exam getExam(String examId) {
        return exams.get(examId);
    }
    
    public synchronized List<Exam> getAllExams() {
        return new ArrayList<>(exams.values());
    }
    
    public synchronized boolean updateExam(Exam exam) {
        if (!exams.containsKey(exam.getId())) {
            return false;
        }
        
        if (exam.getDate().before(new Date())) {
            return false;
        }
        
        Exam oldExam = exams.get(exam.getId());
        if (!oldExam.getBatchId().equals(exam.getBatchId()) || 
            !oldExam.getDate().equals(exam.getDate()) ||
            !oldExam.getStartTime().equals(exam.getStartTime()) ||
            !oldExam.getEndTime().equals(exam.getEndTime()) ||
            !oldExam.getVenue().equals(exam.getVenue())) {
            
            if (hasConflictForUpdate(exam)) {
                return false;
            }
        }
        
        if (!oldExam.getModuleCode().equals(exam.getModuleCode())) {
            List<String> oldModuleExams = moduleExamMap.get(oldExam.getModuleCode());
            if (oldModuleExams != null) {
                oldModuleExams.remove(exam.getId());
            }
            
            moduleExamMap.putIfAbsent(exam.getModuleCode(), new ArrayList<>());
            moduleExamMap.get(exam.getModuleCode()).add(exam.getId());
        }
        
        if (!oldExam.getBatchId().equals(exam.getBatchId())) {
            List<String> oldBatchExams = batchExamMap.get(oldExam.getBatchId());
            if (oldBatchExams != null) {
                oldBatchExams.remove(exam.getId());
            }
            
            batchExamMap.putIfAbsent(exam.getBatchId(), new ArrayList<>());
            batchExamMap.get(exam.getBatchId()).add(exam.getId());
        }
        
        exams.put(exam.getId(), exam);
        saveData();
        return true;
    }
    
    public synchronized boolean deleteExam(String examId) {
        Exam exam = exams.remove(examId);
        if (exam != null) {
            List<String> moduleExams = moduleExamMap.get(exam.getModuleCode());
            if (moduleExams != null) {
                moduleExams.remove(examId);
            }
            
            List<String> batchExams = batchExamMap.get(exam.getBatchId());
            if (batchExams != null) {
                batchExams.remove(examId);
            }
            
            saveData();
            return true;
        }
        return false;
    }
    
    public synchronized List<Exam> getExamsByModule(String moduleCode) {
        List<Exam> result = new ArrayList<>();
        List<String> examIds = moduleExamMap.get(moduleCode);
        if (examIds != null) {
            for (String examId : examIds) {
                Exam exam = exams.get(examId);
                if (exam != null) {
                    result.add(exam);
                }
            }
        }
        result.sort((e1, e2) -> {
            int dateCompare = e1.getDate().compareTo(e2.getDate());
            if (dateCompare != 0) return dateCompare;
            return e1.getStartTime().compareTo(e2.getStartTime());
        });
        return result;
    }
    
    public synchronized List<Exam> getExamsByBatch(String batchId) {
        List<Exam> result = new ArrayList<>();
        List<String> examIds = batchExamMap.get(batchId);
        if (examIds != null) {
            for (String examId : examIds) {
                Exam exam = exams.get(examId);
                if (exam != null) {
                    result.add(exam);
                }
            }
        }
        result.sort((e1, e2) -> {
            int dateCompare = e1.getDate().compareTo(e2.getDate());
            if (dateCompare != 0) return dateCompare;
            return e1.getStartTime().compareTo(e2.getStartTime());
        });
        return result;
    }
    
    public synchronized List<Exam> getUpcomingExams() {
        List<Exam> result = new ArrayList<>();
        Date today = new Date();
        for (Exam exam : exams.values()) {
            if (!exam.getDate().before(today)) {
                result.add(exam);
            }
        }
        result.sort((e1, e2) -> {
            int dateCompare = e1.getDate().compareTo(e2.getDate());
            if (dateCompare != 0) return dateCompare;
            return e1.getStartTime().compareTo(e2.getStartTime());
        });
        return result;
    }
    
    public synchronized List<Exam> getPastExams() {
        List<Exam> result = new ArrayList<>();
        Date today = new Date();
        for (Exam exam : exams.values()) {
            if (exam.getDate().before(today)) {
                result.add(exam);
            }
        }
        result.sort((e1, e2) -> {
            int dateCompare = e2.getDate().compareTo(e1.getDate());
            if (dateCompare != 0) return dateCompare;
            return e2.getStartTime().compareTo(e1.getStartTime());
        });
        return result;
    }
    
    public synchronized boolean hasConflict(Exam newExam) {
        for (Exam exam : exams.values()) {
            if (exam.getBatchId().equals(newExam.getBatchId()) &&
                isSameDay(exam.getDate(), newExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               newExam.getStartTime(), newExam.getEndTime())) {
                    return true;
                }
            }
            
            if (exam.getVenue() != null && newExam.getVenue() != null &&
                exam.getVenue().equalsIgnoreCase(newExam.getVenue()) &&
                isSameDay(exam.getDate(), newExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               newExam.getStartTime(), newExam.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private synchronized boolean hasConflictForUpdate(Exam updatedExam) {
        for (Exam exam : exams.values()) {
            if (exam.getId().equals(updatedExam.getId())) {
                continue;
            }
            
            if (exam.getBatchId().equals(updatedExam.getBatchId()) &&
                isSameDay(exam.getDate(), updatedExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               updatedExam.getStartTime(), updatedExam.getEndTime())) {
                    return true;
                }
            }
            
            if (exam.getVenue() != null && updatedExam.getVenue() != null &&
                exam.getVenue().equalsIgnoreCase(updatedExam.getVenue()) &&
                isSameDay(exam.getDate(), updatedExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               updatedExam.getStartTime(), updatedExam.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    private boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        try {
            int s1 = timeToMinutes(start1);
            int e1 = timeToMinutes(end1);
            int s2 = timeToMinutes(start2);
            int e2 = timeToMinutes(end2);
            
            return s1 < e2 && s2 < e1;
        } catch (Exception e) {
            return false;
        }
    }
    
    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
    
    public synchronized boolean isVenueAvailable(String venue, Date date, 
                                                String startTime, String endTime) {
        for (Exam exam : exams.values()) {
            if (exam.getVenue() != null && exam.getVenue().equalsIgnoreCase(venue) && 
                isSameDay(exam.getDate(), date)) {
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(), startTime, endTime)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public synchronized List<String> getConflictDetails(Exam newExam) {
        List<String> conflicts = new ArrayList<>();
        
        for (Exam exam : exams.values()) {
            if (exam.getBatchId().equals(newExam.getBatchId()) &&
                isSameDay(exam.getDate(), newExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               newExam.getStartTime(), newExam.getEndTime())) {
                    conflicts.add("Batch conflict: " + exam.getId() + " for " + exam.getModuleCode());
                }
            }
            
            if (exam.getVenue() != null && newExam.getVenue() != null &&
                exam.getVenue().equalsIgnoreCase(newExam.getVenue()) &&
                isSameDay(exam.getDate(), newExam.getDate())) {
                
                if (timeOverlaps(exam.getStartTime(), exam.getEndTime(),
                               newExam.getStartTime(), newExam.getEndTime())) {
                    conflicts.add("Venue conflict: " + exam.getId() + " at " + exam.getVenue());
                }
            }
        }
        
        return conflicts;
    }
    
    public synchronized Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        Date today = new Date();
        
        stats.put("total", exams.size());
        stats.put("upcoming", (int) exams.values().stream()
            .filter(e -> !e.getDate().before(today))
            .count());
        stats.put("past", (int) exams.values().stream()
            .filter(e -> e.getDate().before(today))
            .count());
        stats.put("modules", moduleExamMap.size());
        stats.put("batches", batchExamMap.size());
        return stats;
    }
}