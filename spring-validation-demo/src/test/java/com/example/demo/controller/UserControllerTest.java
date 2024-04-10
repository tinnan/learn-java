package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

@WebMvcTest
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPostRequestToUserWithValidUser_thenCorrectResponse() throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        String userJson = """
                {
                    "name": "John Doe",
                    "email": "john.d@gmail.com",
                    "age": 25
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(textPlainUtf8))
                .andExpect(MockMvcResultMatchers.content()
                        .string("User is valid."));
        User expectedSavedUser = new User();
        expectedSavedUser.setName("John Doe");
        expectedSavedUser.setEmail("john.d@gmail.com");
        expectedSavedUser.setAge(25);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(expectedSavedUser);
    }

    @Test
    public void whenPostRequestToUserWithInvalidUser_thenCorrectResponse() throws Exception {
        String userJson = """
                {
                    "name": "John Doe",
                    "age": 25
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json("""
                                {
                                    "email": "Email is mandatory"
                                }
                                """));
    }

    @TestConfiguration
    public static class UserControllerTestConfiguration {
        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:validation/CustomValidationMessage");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        }
    }
}