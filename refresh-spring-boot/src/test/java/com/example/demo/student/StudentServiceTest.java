package com.example.demo.student;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StudentServiceTest {

    @TestConfiguration
    public static class StudentServiceTestConfiguration {
        @Bean
        public StudentService studentService(StudentRepository studentRepository) {
            return new StudentService(studentRepository);
        }
    }

    @Autowired
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @Before
    public void setUp() {
        Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 07));
        Student john = new Student("John", "john@gmail.com", LocalDate.of(2000, Month.JANUARY, 24));
        Mockito.when(studentRepository.findAll()).thenReturn(List.of(tin, john));
    }

    @Test
    public void testGetStudents() {
        List<Student> students = studentService.getStudents();

        assertEquals("Tin", students.get(0).getName());
        assertEquals("John", students.get(1).getName());
    }

    @Test
    public void testRegisterStudent() {
        Student newStudent = new Student("Rachel", "rachel@gmail.com", LocalDate.of(1989, Month.MAY, 15));
        studentService.registerStudent(newStudent);

        Mockito.verify(studentRepository, Mockito.times(1)).save(newStudent);
    }

    @Test
    public void testUnregisterStudent() {
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);

        studentService.unregisterStudent(1L);

        Mockito.verify(studentRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateStudent() {
        Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 07));
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(tin));
        Mockito.when(studentRepository.findStudentByEmail("jess@gmail.com")).thenReturn(Optional.empty());

        studentService.updateStudent(1L, "Jess", "jess@gmail.com");

        Mockito.verify(studentRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(studentRepository, Mockito.times(1)).findStudentByEmail("jess@gmail.com");
    }
}
