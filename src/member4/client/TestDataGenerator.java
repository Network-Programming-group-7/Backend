package member4.client;

import common.models.Lecturer;
import common.models.Student;
import common.storage.FileManager;
import common.utils.Constants;

public class TestDataGenerator {
    public static void generateLecturerData() {
        // Get the persistence manager
        FileManager fileManager = FileManager.getInstance();

        // 1. Create one VALID Lecturer object for testing
        Lecturer testLecturer = new Lecturer();

        // **IMPORTANT:** These fields must match the fields your Lecturer model uses for authentication.
        // We assume your Lecturer model has getId(), getPassword(), and getName().
        testLecturer.setId("L001");
        testLecturer.setPassword("pass123"); // The password you will use in your client test
        testLecturer.setName("Dr. Test Lecturer");
        testLecturer.setEmail("test@university.edu");
        // Note: You must use the appropriate setters available on your Lecturer model.

        // 2. Clear any existing lecturer data and save the new test lecturer
        fileManager.clearFile(Constants.LECTURERS_FILE); // Ensures a clean start
        fileManager.save(testLecturer, Constants.LECTURERS_FILE);

        System.out.println("✅ Saved test lecturer L001 to " + Constants.LECTURERS_FILE);


    }
    public static void generateStudentAndModuleData() {
        FileManager fileManager = FileManager.getInstance();

        // --- 1. Create Test Student ---
        Student testStudent = new Student();
        testStudent.setId("S001"); // The Student ID you will use in your test
        testStudent.setName("Test Student One");
        // Note: Set other mandatory fields based on your Student model if necessary

        fileManager.clearFile(Constants.STUDENTS_FILE);
        fileManager.save(testStudent, Constants.STUDENTS_FILE);
        System.out.println("✅ Saved test student S001 to " + Constants.STUDENTS_FILE);

        // --- 2. Create Test Module ---
        common.models.Module testModule = new common.models.Module();
        testModule.setCode("CS101"); // The Module Code you will use in your test
        testModule.setName("Intro to Testing");
        testModule.setCredits(3); // Needed if GradeCalculator or GPA is complex
        // Note: Set other mandatory fields based on your Module model if necessary

        fileManager.clearFile(Constants.MODULES_FILE);
        fileManager.save(testModule, Constants.MODULES_FILE);
        System.out.println("✅ Saved test module CS101 to " + Constants.MODULES_FILE);
    }



    public static void main(String[] args) {
        // NOTE: Ensure Constants.DATA_DIR and Constants.LECTURERS_FILE are defined!
        generateLecturerData();
        generateStudentAndModuleData();
    }
}
