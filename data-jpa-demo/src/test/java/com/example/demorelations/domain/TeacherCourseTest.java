package com.example.demorelations.domain;

import com.example.demorelations.repo.CourseRepository;
import com.example.demorelations.repo.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

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

    @Test
    public void givenTeacherAndCourseData_whenPersist_thenSuccess() {
        Teacher teacher = new Teacher(null, "John", "Doe");
        Course course = new Course("JAVA101", "Java 101", teacher);
        // This works because "cascade" option on field "teacher" in Course entity is set to PERSIST.
        // Otherwise, it would have thrown PersistentObjectException because teacher is not a managed entity.
        // In that case, you will have to persist Teacher object first then persist Course object.
        courseRepository.save(course);

        Optional<Course> savedCourse = courseRepository.findById(course.getId());
        assertTrue(savedCourse.isPresent());
        assertNotNull(savedCourse.get().getTeacher());
        // Teacher#getId() can return auto-generated ID here because "cascade" option on field "teacher" in Course
        // entity is set to REFRESH.
        assertEquals(1, savedCourse.get().getTeacher().getId());
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
                .put("BA-ME101", new Course("BA-ME101", "Macro Economic 101", teacher));
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