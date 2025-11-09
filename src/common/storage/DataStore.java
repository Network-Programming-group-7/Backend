package common.storage;

import common.models.Module;
import common.models.ModuleRegistration;
import common.models.Student;
import common.utils.ValidationUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataStore {
    private static final int MAX_CREDITS_PER_SEM = 30;

    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Module> modules = new ConcurrentHashMap<>();
    private final Map<String, List<ModuleRegistration>> registrationsByStudent = new ConcurrentHashMap<>();

    private final Path dataDir = Paths.get("data");
    private final Path studentsFile = dataDir.resolve("students.dat");
    private final Path modulesFile = dataDir.resolve("modules.dat");
    private final Path regsFile = dataDir.resolve("registrations.dat");

    public DataStore() {
        loadAll();
    }

    public synchronized void loadAll() {
        students.clear();
        students.putAll(PersistenceManager.load(studentsFile, new HashMap<>()));
        modules.clear();
        modules.putAll(PersistenceManager.load(modulesFile, new HashMap<>()));
        registrationsByStudent.clear();
        registrationsByStudent.putAll(PersistenceManager.load(regsFile, new HashMap<>()));
    }

    private synchronized void persistAll() {
        try {
            PersistenceManager.save(studentsFile, new HashMap<>(students));
            PersistenceManager.save(modulesFile, new HashMap<>(modules));
            PersistenceManager.save(regsFile, new HashMap<>(registrationsByStudent));
        } catch (IOException e) {
            // Log to stderr for simplicity
            System.err.println("Persistence error: " + e.getMessage());
        }
    }

    // Student CRUD
    public synchronized boolean addStudent(Student s) {
        if (s == null || s.getId() == null || s.getId().isEmpty()) return false;
        if (!ValidationUtils.isValidEmail(s.getEmail())) return false;
        if (students.containsKey(s.getId())) return false;
        students.put(s.getId(), s);
        persistAll();
        return true;
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public synchronized boolean updateStudentField(String id, String field, String value) {
        Student s = students.get(id);
        if (s == null) return false;
        switch (field.toLowerCase(Locale.ROOT)) {
            case "name": s.setName(value); break;
            case "email": if (!ValidationUtils.isValidEmail(value)) return false; s.setEmail(value); break;
            case "batch": s.setBatch(value); break;
            case "phone": s.setPhone(value); break;
            case "enrollmentdate": s.setEnrollmentDate(LocalDate.parse(value)); break;
            default: return false;
        }
        persistAll();
        return true;
    }

    public synchronized boolean deleteStudent(String id) {
        if (!students.containsKey(id)) return false;
        students.remove(id);
        registrationsByStudent.remove(id);
        persistAll();
        return true;
    }

    public List<Student> listStudents() {
        return new ArrayList<>(students.values());
    }

    // Module CRUD
    public synchronized boolean addModule(Module m) {
        if (m == null || m.getCode() == null || m.getCode().isEmpty()) return false;
        if (m.getCredits() <= 0) return false;
        if (modules.containsKey(m.getCode())) return false;
        modules.put(m.getCode(), m);
        persistAll();
        return true;
    }

    public Module getModule(String code) {
        return modules.get(code);
    }

    public synchronized boolean updateModuleField(String code, String field, String value) {
        Module m = modules.get(code);
        if (m == null) return false;
        switch (field.toLowerCase(Locale.ROOT)) {
            case "name": m.setName(value); break;
            case "credits": m.setCredits(Integer.parseInt(value)); break;
            case "lecturer": m.setLecturer(value); break;
            case "semester": m.setSemester(Integer.parseInt(value)); break;
            case "description": m.setDescription(value); break;
            default: return false;
        }
        persistAll();
        return true;
    }

    public synchronized boolean deleteModule(String code) {
        if (!modules.containsKey(code)) return false;
        modules.remove(code);
        // Clean dependent registrations
        registrationsByStudent.values().forEach(list ->
                list.removeIf(r -> r.getModuleCode().equals(code)));
        persistAll();
        return true;
    }

    public List<Module> listModules() {
        return new ArrayList<>(modules.values());
    }

    // Registration
    public synchronized String registerModule(String studentId, String moduleCode, int semester) {
        Student s = students.get(studentId);
        if (s == null) return "ERROR|StudentNotFound";
        Module m = modules.get(moduleCode);
        if (m == null) return "ERROR|ModuleNotFound";
        if (m.getSemester() != semester) return "ERROR|SemesterMismatch";

        List<ModuleRegistration> regs = registrationsByStudent.computeIfAbsent(studentId, k -> new ArrayList<>());

        if (regs.stream().anyMatch(r -> r.getSemester() == semester && r.getModuleCode().equals(moduleCode)))
            return "ERROR|DuplicateRegistration";

        int currentCredits = regs.stream()
                .filter(r -> r.getSemester() == semester)
                .map(ModuleRegistration::getModuleCode)
                .map(modules::get)
                .filter(Objects::nonNull)
                .mapToInt(Module::getCredits)
                .sum();

        if (currentCredits + m.getCredits() > MAX_CREDITS_PER_SEM)
            return "ERROR|MaxCreditsExceeded";

        // Hook: check prerequisites/conflicts if needed
        if (!ValidationUtils.hasMetPrerequisites(this, studentId, moduleCode))
            return "ERROR|PrerequisiteNotMet";

        regs.add(new ModuleRegistration(studentId, moduleCode, semester));
        persistAll();
        return "OK|Registered";
    }

    public List<Module> listAvailableModulesForStudent(String studentId, int semester) {
        Student s = students.get(studentId);
        if (s == null) return Collections.emptyList();
        Set<String> already = registrationsByStudent.getOrDefault(studentId, List.of()).stream()
                .filter(r -> r.getSemester() == semester)
                .map(ModuleRegistration::getModuleCode)
                .collect(Collectors.toSet());
        return modules.values().stream()
                .filter(m -> m.getSemester() == semester)
                .filter(m -> !already.contains(m.getCode()))
                .collect(Collectors.toList());
    }

    public List<ModuleRegistration> listRegistrationsForStudent(String studentId, int semester) {
        return registrationsByStudent.getOrDefault(studentId, List.of()).stream()
                .filter(r -> r.getSemester() == semester)
                .collect(Collectors.toList());
    }
}