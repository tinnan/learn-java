package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.UserService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPostRequestToUserWithValidUser_thenCorrectResponse() throws Exception {
        String acceptLang = "en-EN";
        String jsonBody = """
                {
                    "name": "John Doe",
                    "email": "john.d@gmail.com",
                    "age": 25
                }
                """;
        MediaType requestType = MediaType.APPLICATION_JSON;
        int responseData = 200;
        MediaType responseType = MediaType.APPLICATION_JSON;
        String responseJson = """
                {
                    "message": "User is valid."
                }
                """;
        performRequest(acceptLang, jsonBody, requestType, responseData, responseType, responseJson);

        User expectedSavedUser = new User();
        expectedSavedUser.setName("John Doe");
        expectedSavedUser.setEmail("john.d@gmail.com");
        expectedSavedUser.setAge(25);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(expectedSavedUser);
    }

    @Test
    public void whenPostRequestToUserWithInvalidUser_thenReturnBadRequest_EN() throws Exception {
        String acceptLang = "en-EN";
        String jsonBody = """
                {
                    "name": "John Doe",
                    "age": 25
                }
                """;
        MediaType requestType = MediaType.APPLICATION_JSON;
        int responseStatus = 400;
        MediaType responseType = MediaType.APPLICATION_JSON;
        String responseJson = """
                {
                    "email": "Email is mandatory"
                }
                """;
        performRequest(acceptLang, jsonBody, requestType, responseStatus, responseType, responseJson);
    }

    @Test
    public void whenPostRequestToUserWithInvalidUser_thenReturnBadRequest_TH() throws Exception {
        String acceptLang = "th-TH";
        String jsonBody = """
                {
                    "name": "John Doe",
                    "age": 25
                }
                """;
        MediaType requestType = MediaType.APPLICATION_JSON;
        int responseStatus = 400;
        MediaType responseType = MediaType.APPLICATION_JSON;
        String responseJson = """
                {
                    "email": "Email จำเป็นต้องกรอก"
                }
                """;
        performRequest(acceptLang, jsonBody, requestType, responseStatus, responseType, responseJson);
    }

    @Test
    public void whenPostRequestToUserWithInvalidAddressData_thenReturnBadRequest_TH() throws Exception {
        String acceptLang = "th-TH";
        String jsonBody = """
                {
                    "name": "John Doe",
                    "email": "john.d@gmail.com",
                    "age": 25,
                    "addressList": [
                        {"addressNo": ""}
                    ]
                }
                """;
        MediaType requestType = MediaType.APPLICATION_JSON;
        int responseStatus = 400;
        MediaType responseType = MediaType.APPLICATION_JSON;
        String responseJson = """
                {
                    "addressList[0].addressNo":"must not be blank"
                }
                """;
        performRequest(acceptLang, jsonBody, requestType, responseStatus, responseType, responseJson);
    }

    private void performRequest(String acceptLang, String jsonBody, MediaType requestType,
        int status, MediaType responseType, String responseJson)
        throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .header(HttpHeaders.ACCEPT_LANGUAGE, acceptLang)
                .content(jsonBody)
                .contentType(requestType))
            .andExpect(MockMvcResultMatchers.status()
                .is(status))
            .andExpect(MockMvcResultMatchers.content()
                .contentType(responseType))
            .andExpect(MockMvcResultMatchers.content()
                .json(responseJson));
    }

    @TestConfiguration
    public static class TestMessageConfig {
        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:validation/CustomValidationMessage");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        }
    }
}