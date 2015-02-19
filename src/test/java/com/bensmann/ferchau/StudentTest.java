package com.bensmann.ferchau;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

public class StudentTest {

    @Test
    public void sortTest() {
        Student[] students = {
                new Student("0054", "Albert Einstein"),
                new Student("1234", "Gottfried Wilhelm Leibniz"),
                new Student("5421", "Carl Friedrich Gauss")
        };
        Arrays.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Student student : students) {
            System.out.println(student);
        }
    }

}
