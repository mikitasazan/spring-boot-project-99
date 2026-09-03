package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserUpdateDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

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
	}

	@Test
	void indexWithoutTokenReturns401() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createReturnsCreatedUserWithoutPassword() throws Exception {
		var data = new UserCreateDTO("jack@google.com", "Jack", "Jons", "some-password");

		mockMvc.perform(post("/api/users")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("jack@google.com"))
				.andExpect(jsonPath("$.firstName").value("Jack"))
				.andExpect(jsonPath("$.password").doesNotExist());

		var saved = userRepository.findByEmail("jack@google.com").orElseThrow();
		assertThat(saved.getPassword()).isNotEqualTo("some-password");
		assertThat(passwordEncoder.matches("some-password", saved.getPassword())).isTrue();
	}

	@Test
	void createWithInvalidDataReturns400() throws Exception {
		var data = new UserCreateDTO("not-an-email", null, null, "ab");

		mockMvc.perform(post("/api/users")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void indexListsUsersWithoutPasswords() throws Exception {
		createUser("a@example.com", "secret1");
		createUser("b@example.com", "secret2");

		mockMvc.perform(get("/api/users").with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].password").doesNotExist())
				.andExpect(jsonPath("$[1].password").doesNotExist());
	}

	@Test
	void showReturnsOneUser() throws Exception {
		var id = createUser("show@example.com", "secretpw").getId();

		mockMvc.perform(get("/api/users/" + id).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("show@example.com"));
	}

	@Test
	void showMissingUserReturns404() throws Exception {
		mockMvc.perform(get("/api/users/999999").with(user("someone@example.com")))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateChangesOnlySentFields() throws Exception {
		var user = createUser("keep@example.com", "secretpw");
		user.setFirstName("Original");
		userRepository.save(user);

		var data = new UserUpdateDTO(null, null, "NewLastName", null);

		mockMvc.perform(put("/api/users/" + user.getId())
						.with(user("keep@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("keep@example.com"))
				.andExpect(jsonPath("$.firstName").value("Original"))
				.andExpect(jsonPath("$.lastName").value("NewLastName"));
	}

	@Test
	void updateAnotherUserReturns403() throws Exception {
		var target = createUser("victim@example.com", "secretpw");
		var data = new UserUpdateDTO(null, null, "Hacked", null);

		mockMvc.perform(put("/api/users/" + target.getId())
						.with(user("attacker@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isForbidden());
	}

	@Test
	void deleteRemovesUser() throws Exception {
		var id = createUser("delete@example.com", "secretpw").getId();

		mockMvc.perform(delete("/api/users/" + id).with(user("delete@example.com")))
				.andExpect(status().isNoContent());

		assertThat(userRepository.existsById(id)).isFalse();
	}

	@Test
	void deleteAnotherUserReturns403() throws Exception {
		var target = createUser("victim2@example.com", "secretpw");

		mockMvc.perform(delete("/api/users/" + target.getId()).with(user("attacker@example.com")))
				.andExpect(status().isForbidden());

		assertThat(userRepository.existsById(target.getId())).isTrue();
	}

	private hexlet.code.app.model.User createUser(String email, String password) {
		var savedUser = new hexlet.code.app.model.User();
		savedUser.setEmail(email);
		savedUser.setPassword(passwordEncoder.encode(password));
		return userRepository.save(savedUser);
	}

}
