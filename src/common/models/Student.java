package common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String batch;
    private String phone;
    private LocalDate enrollmentDate;

    public Student() {}

    public Student(String id, String name, String email, String batch, String phone, LocalDate enrollmentDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.batch = batch;
        this.phone = phone;
        this.enrollmentDate = enrollmentDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return Objects.equals(id, s.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        return "Student{id='" + id + "', name='" + name + "', email='" + email + "', batch='" + batch +
                "', phone='" + phone + "', enrollmentDate=" + enrollmentDate + "}";
    }
}