package com.example.demorelations.domain;

import com.example.demorelations.repo.CourseRepository;
import com.example.demorelations.repo.StudentRepository;
import com.example.demorelations.repo.TeacherRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class TeacherCourseTest {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void givenDataToSave_whenPersist_thenSuccess() {
        Teacher teacher = new Teacher(null, "John", "Doe");
        CourseMaterial courseMaterial = new CourseMaterial(null, "https://github.com/uni/course/materials/java101",
                null);
        Student student1 = new Student(null, "Sarah", "Liu", LocalDate.of(2005, 3, 1), true, null);
        Course course = new Course("JAVA101", "Java 101", teacher, courseMaterial, List.of(student1));
        courseMaterial.setCourse(course);
        student1.setCourses(List.of(course));
        // This works because "cascade" option on field "teacher" in Course entity is set to PERSIST.
        // Otherwise, it would have thrown PersistentObjectException because teacher is not a managed entity.
        // In that case, you will have to persist Teacher object first then persist Course object.
        courseRepository.save(course);

        Optional<Course> savedCourseOpt = courseRepository.findById(course.getId());
        assertTrue(savedCourseOpt.isPresent());
        Course savedCourse = savedCourseOpt.get();
        assertNotNull(savedCourse.getTeacher());
        // Teacher#getId() can return auto-generated ID here because "cascade" option on field "teacher" in Course
        // entity is set to REFRESH.
        assertEquals(1, savedCourse.getTeacher()
                .getId());
        assertNotNull(savedCourse.getCourseMaterial());
        assertEquals("https://github.com/uni/course/materials/java101", savedCourse.getCourseMaterial()
                .getUrl());

        assertNotNull(savedCourse.getStudents().get(0));
        assertEquals(1, savedCourse.getStudents().get(0).getId());
        assertEquals("Sarah", savedCourse.getStudents().get(0).getFirstName());

        Optional<Student> studentOpt = studentRepository.findById(savedCourse.getStudents()
                .get(0)
                .getId());
        assertTrue(studentOpt.isPresent());
        Student student = studentOpt.get();
        assertNotNull(student.getCourses().get(0));
        assertEquals("JAVA101", student.getCourses().get(0).getId());
    }

    @Test
    @Sql("/test-data/relations/teacher.sql")
    public void givenInputData_whenOperateOnData_thenSuccess() {
        Optional<Teacher> teacherOpt = teacherRepository.findById(1);
        assertTrue(teacherOpt.isPresent());
        Teacher teacher = teacherOpt.get();
        assertEquals("Jane", teacher.getFirstName());
        assertEquals("Doe", teacher.getLastName());
        assertThat(teacher.getCourses()
                .values()).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java 101", "SQL 101",
                        "JPA 101");

        // Update course title through course objects inside the teacher object.
        teacher.getCourses()
                .values()
                .forEach(c -> c.setTitle(c.getTitle() + " - test"));
        // Insert new course for this teacher. This works because cascase type is set to ALL.
        teacher.getCourses()
                .put("BA-ME101", new Course("BA-ME101", "Macro Economic 101", teacher, null, null));
        teacherRepository.save(teacher);

        Optional<Course> courseOpt = courseRepository.findById("CS-JV101");
        assertTrue(courseOpt.isPresent());
        Course course = courseOpt.get();
        // Course title should be an updated version.
        assertThat(course.getTitle()).isEqualTo("Java 101 - test");
        assertThat(course.getTeacher()
                .getId()).isEqualTo(1);
        assertThat(course.getTeacher()
                .getFirstName()).isEqualTo("Jane");
        assertThat(course.getTeacher()
                .getLastName()).isEqualTo("Doe");

        // New course should be inserted into database.
        courseOpt = courseRepository.findById("BA-ME101");
        assertTrue(courseOpt.isPresent());
        course = courseOpt.get();
        assertThat(course.getTitle()).isEqualTo("Macro Economic 101");
        assertThat(course.getTeacher()
                .getId()).isEqualTo(1);
        assertThat(course.getTeacher()
                .getFirstName()).isEqualTo("Jane");
        assertThat(course.getTeacher()
                .getLastName()).isEqualTo("Doe");
    }
}