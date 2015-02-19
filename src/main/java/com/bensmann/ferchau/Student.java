package com.bensmann.ferchau;

import java.util.Objects;

public class Student {

    private String studentId;

    private String name;

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Student{studentId='%s', name='%s'}", studentId, name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Student other = (Student) obj;
        return Objects.equals(this.studentId, other.studentId) && Objects.equals(this.name, other.name);
    }

}
