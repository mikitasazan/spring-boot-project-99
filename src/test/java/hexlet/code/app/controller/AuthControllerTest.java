package hexlet.code.app.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LoginDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
		userRepository.deleteAll();

		var user = new User();
		user.setEmail("login@example.com");
		user.setPassword(passwordEncoder.encode("some-password"));
		userRepository.save(user);
	}

	@Test
	void loginWithValidCredentialsReturnsToken() throws Exception {
		var data = new LoginDTO("login@example.com", "some-password");

		var result = mockMvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isOk())
				.andReturn();

		var token = result.getResponse().getContentAsString();
		assertThat(token).isNotBlank();
		assertThat(token.split("\\.")).hasSize(3);
	}

	@Test
	void loginWithWrongPasswordReturns401() throws Exception {
		var data = new LoginDTO("login@example.com", "wrong-password");

		mockMvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginWithUnknownUserReturns401() throws Exception {
		var data = new LoginDTO("ghost@example.com", "some-password");

		mockMvc.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

}
