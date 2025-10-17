package common.utils;

public class Constants {
    // Server Ports
    public static final int ADMIN_PORT = 8001;
    public static final int STUDENT_MODULE_PORT = 8002;
    public static final int EXAM_PORT = 8003;
    public static final int RESULT_PORT = 8004;
    public static final int STUDENT_PORTAL_PORT = 8005;
    
    // Server Host
    public static final String SERVER_HOST = "localhost";
    
    // Data Directory
    public static final String DATA_DIR = "data/";
    
    // File Names
    public static final String USERS_FILE = "users.dat";
    public static final String LECTURERS_FILE = "lecturers.dat";
    public static final String STUDENTS_FILE = "students.dat";
    public static final String BATCHES_FILE = "batches.dat";
    public static final String MODULES_FILE = "modules.dat";
    public static final String EXAMS_FILE = "exams.dat";
    public static final String RESULTS_FILE = "results.dat";
    public static final String REGISTRATIONS_FILE = "registrations.dat";
    
    // Marks Configuration
    public static final int MAX_MARKS = 100;
    public static final int MIN_MARKS = 0;
    public static final int PASS_MARKS = 40;
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_LECTURER = "LECTURER";
    public static final String ROLE_STUDENT = "STUDENT";
    
    // Response Status
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String FAILED = "FAILED";
    
    // Request Types - Admin Management (Member 1)
    public static final String ADD_LECTURER = "ADD_LECTURER";
    public static final String VIEW_LECTURER = "VIEW_LECTURER";
    public static final String LIST_LECTURERS = "LIST_LECTURERS";
    public static final String UPDATE_LECTURER = "UPDATE_LECTURER";
    public static final String DELETE_LECTURER = "DELETE_LECTURER";
    public static final String ADD_BATCH = "ADD_BATCH";
    public static final String VIEW_BATCH = "VIEW_BATCH";
    public static final String LIST_BATCHES = "LIST_BATCHES";
    public static final String UPDATE_BATCH = "UPDATE_BATCH";
    public static final String DELETE_BATCH = "DELETE_BATCH";
    public static final String ADD_STUDENTS_TO_BATCH = "ADD_STUDENTS_TO_BATCH";
    
    // Request Types - Student & Module Management (Member 2)
    public static final String ADD_STUDENT = "ADD_STUDENT";
    public static final String VIEW_STUDENT = "VIEW_STUDENT";
    public static final String LIST_STUDENTS = "LIST_STUDENTS";
    public static final String UPDATE_STUDENT = "UPDATE_STUDENT";
    public static final String DELETE_STUDENT = "DELETE_STUDENT";
    public static final String ADD_MODULE = "ADD_MODULE";
    public static final String VIEW_MODULE = "VIEW_MODULE";
    public static final String LIST_MODULES = "LIST_MODULES";
    public static final String UPDATE_MODULE = "UPDATE_MODULE";
    public static final String DELETE_MODULE = "DELETE_MODULE";
    public static final String REGISTER_MODULE = "REGISTER_MODULE";
    public static final String GET_STUDENT_MODULES = "GET_STUDENT_MODULES";
    public static final String GET_AVAILABLE_MODULES = "GET_AVAILABLE_MODULES";
    
    // Request Types - Exam Management (Member 3)
    public static final String ADD_EXAM = "ADD_EXAM";
    public static final String VIEW_EXAM = "VIEW_EXAM";
    public static final String LIST_EXAMS = "LIST_EXAMS";
    public static final String UPDATE_EXAM = "UPDATE_EXAM";
    public static final String DELETE_EXAM = "DELETE_EXAM";
    public static final String GET_MODULE_EXAMS = "GET_MODULE_EXAMS";
    public static final String GET_EXAM_SCHEDULE = "GET_EXAM_SCHEDULE";
    
    // Request Types - Result Management (Member 4)
    public static final String ADD_RESULT = "ADD_RESULT";
    public static final String VIEW_RESULT = "VIEW_RESULT";
    public static final String LIST_RESULTS = "LIST_RESULTS";
    public static final String UPDATE_RESULT = "UPDATE_RESULT";
    public static final String DELETE_RESULT = "DELETE_RESULT";
    public static final String UPLOAD_CSV = "UPLOAD_CSV";
    public static final String GET_STUDENT_RESULTS = "GET_STUDENT_RESULTS";
    
    // Request Types - Student Portal (Member 5)
    public static final String VIEW_MY_RESULTS = "VIEW_MY_RESULTS";
    public static final String VIEW_MODULE_RESULT = "VIEW_MODULE_RESULT";
    public static final String CALCULATE_GPA = "CALCULATE_GPA";
    public static final String VIEW_REGISTERED_MODULES = "VIEW_REGISTERED_MODULES";
    public static final String VIEW_AVAILABLE_MODULES = "VIEW_AVAILABLE_MODULES";
    public static final String DOWNLOAD_TRANSCRIPT = "DOWNLOAD_TRANSCRIPT";
}