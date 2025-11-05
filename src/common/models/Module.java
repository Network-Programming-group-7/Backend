package common.models;

import java.io.Serializable;
import java.util.Objects;

public class Module implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private int credits;
    private String lecturer;
    private int semester;
    private String description;

    public Module() {}

    public Module(String code, String name, int credits, String lecturer, int semester, String description) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.lecturer = lecturer;
        this.semester = semester;
        this.description = description;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getLecturer() { return lecturer; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module m = (Module) o;
        return Objects.equals(code, m.code);
    }

    @Override public int hashCode() { return Objects.hash(code); }

    @Override public String toString() {
        return "Module{code='" + code + "', name='" + name + "', credits=" + credits +
                ", lecturer='" + lecturer + "', semester=" + semester + ", description='" + description + "'}";
    }
}