package com.example.demo.student;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StudentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testFindStudentByEmail() {
        Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 7));
        Student john = new Student("John", "john@gmail.com", LocalDate.of(2000, Month.JANUARY, 25));
        testEntityManager.persist(tin);
        testEntityManager.persist(john);
        testEntityManager.flush();

        Optional<Student> student = studentRepository.findStudentByEmail("tin@gmail.com");

        assertTrue(student.isPresent());
        assertEquals("Tin", student.get().getName());
    }
}
