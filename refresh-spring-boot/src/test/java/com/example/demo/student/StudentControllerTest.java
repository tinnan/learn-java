package com.example.demo.student;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StudentService studentService;

    @Test
    public void testGetStudents() throws Exception {
        Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 07));
        Student john = new Student("John", "john@gmail.com", LocalDate.of(2000, Month.JANUARY, 24));
        Mockito.when(studentService.getStudents()).thenReturn(List.of(tin, john));

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

        Student expectedRegisteringStudent = new Student("Rachel", "rachel@gmail.com", LocalDate.of(1998, 7, 14));
        Mockito.verify(studentService, Mockito.times(1))
                .registerStudent(ArgumentMatchers.refEq(expectedRegisteringStudent));
    }

    @Test
    public void testUnregisterStudent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(studentService, Mockito.times(1)).unregisterStudent(1L);
    }

    @Test
    public void testUpdateStudent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", "1").param("name", "Jane")
                .param("email", "jane@gmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(studentService, Mockito.times(1)).updateStudent(1L, "Jane", "jane@gmail.com");
    }
}
