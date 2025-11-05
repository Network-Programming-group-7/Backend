package common.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ModuleRegistration implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String moduleCode;
    private int semester;
    private LocalDateTime registeredAt;

    public ModuleRegistration() {}

    public ModuleRegistration(String studentId, String moduleCode, int semester) {
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.semester = semester;
        this.registeredAt = LocalDateTime.now();
    }

    public String getStudentId() { return studentId; }
    public String getModuleCode() { return moduleCode; }
    public int getSemester() { return semester; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleRegistration)) return false;
        ModuleRegistration that = (ModuleRegistration) o;
        return semester == that.semester &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(moduleCode, that.moduleCode);
    }

    @Override public int hashCode() { return Objects.hash(studentId, moduleCode, semester); }

    @Override public String toString() {
        return "ModuleRegistration{studentId='" + studentId + "', moduleCode='" + moduleCode +
                "', semester=" + semester + ", registeredAt=" + registeredAt + "}";
    }
}