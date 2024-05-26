package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ValidateOnServiceIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void whenGetRequestWithInvalidEmailFormat_thenReturnBadRequest_EN() throws Exception {
		String acceptLang = "en-EN";
		String requestEmail = "tin.n@";
		int responseStatus = 400;
		MediaType responseType = MediaType.APPLICATION_JSON;
		String responseJson = """
                {
                    "getUser.email": "Email is invalid format"
                }
                """;
		performRequest(acceptLang, requestEmail, responseStatus, responseType, responseJson);
	}

	@Test
	public void whenGetRequestWithInvalidEmailFormat_thenReturnBadRequest_TH() throws Exception {
		String acceptLang = "th-TH";
		String requestEmail = "tin.n@";
		int responseStatus = 400;
		MediaType responseType = MediaType.APPLICATION_JSON;
		String responseJson = """
                {
                    "getUser.email": "รูปแบบของ Email ไม่ถูกต้อง"
                }
                """;
		performRequest(acceptLang, requestEmail, responseStatus, responseType, responseJson);
	}

	private void performRequest(String acceptLang, String requestEmail,
		int status, MediaType responseType, String responseJson)
		throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
				.header(HttpHeaders.ACCEPT_LANGUAGE, acceptLang)
				.param("email", requestEmail))
			.andExpect(MockMvcResultMatchers.status()
				.is(status))
			.andExpect(MockMvcResultMatchers.content()
				.contentType(responseType))
			.andExpect(MockMvcResultMatchers.content()
				.json(responseJson));
	}
}
