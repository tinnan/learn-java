package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.student.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DemoApplication.class)
@AutoConfigureMockMvc
// This one is not needed because in this project we actually use H2 database.
// @TestPropertySource(locations =
// "classpath:application-integrationtest.properties")
public class DemoApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentRepository studentRepository;

	@Before
	public void beforeEach() {
		studentRepository.deleteAll();
	}

	private void addStudents() {
		Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 07));
		Student john = new Student("John", "john@gmail.com", LocalDate.of(2000, Month.JANUARY, 24));
		studentRepository.saveAll(List.of(tin, john));
	}

	@Test
	public void testGetStudents() throws Exception {
		addStudents();

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/student").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$[*].name", Matchers.containsInAnyOrder("Tin", "John")));
	}

	@Test
	public void testRegisterNewStudent() throws Exception {
		mvc.perform(
				MockMvcRequestBuilders.post("/api/v1/student").contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\": \"Rachel\", \"email\": \"rachel@gmail.com\", \"dob\": \"1998-07-14\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk());

		List<Student> students = studentService.getStudents();
		assertEquals("Rachel", students.get(0).getName());
	}

	@Test
	public void testUnregisterStudent() throws Exception {
		addStudents();

		Optional<Student> student = studentRepository.findStudentByEmail("tin@gmail.com");

		if (!student.isPresent()) {
			throw new IllegalStateException("Not found student");
		}

		mvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}", student.get().getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Optional<Student> updatedStudent = studentRepository.findById(student.get().getId());
		assertFalse(updatedStudent.isPresent());
	}

	@Test
	public void testUpdateStudent() throws Exception {
		addStudents();

		Optional<Student> student = studentRepository.findStudentByEmail("tin@gmail.com");

		mvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", student.get().getId()).param("name", "Jane")
				.param("email", "jane@gmail.com")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Optional<Student> updatedStudent = studentRepository.findById(student.get().getId());
		assertTrue(updatedStudent.isPresent());
		assertEquals("Jane", updatedStudent.get().getName());
		assertEquals("jane@gmail.com", updatedStudent.get().getEmail());
	}
}
