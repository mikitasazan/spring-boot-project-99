package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class TaskStatusControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@Test
	void indexWithoutTokenReturns401() throws Exception {
		mockMvc.perform(get("/api/task_statuses"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createRequiresAuth() throws Exception {
		var data = new TaskStatusCreateDTO("Unauthorized status", "unauthorized-status");

		mockMvc.perform(post("/api/task_statuses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createReturnsCreatedStatus() throws Exception {
		var data = new TaskStatusCreateDTO("In progress", "in-progress");

		mockMvc.perform(post("/api/task_statuses")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("In progress"))
				.andExpect(jsonPath("$.slug").value("in-progress"))
				.andExpect(jsonPath("$.id").exists());

		assertThat(taskStatusRepository.findBySlug("in-progress")).isPresent();
	}

	@Test
	void createWithBlankNameReturns400() throws Exception {
		var data = new TaskStatusCreateDTO("", "blank-name-status");

		mockMvc.perform(post("/api/task_statuses")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void showReturnsOneStatus() throws Exception {
		var status = createStatus("Show me", "show-me");

		mockMvc.perform(get("/api/task_statuses/" + status.getId()).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.slug").value("show-me"));
	}

	@Test
	void showMissingStatusReturns404() throws Exception {
		mockMvc.perform(get("/api/task_statuses/999999").with(user("someone@example.com")))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateChangesOnlySentFields() throws Exception {
		var status = createStatus("Old name", "keep-slug");
		var data = new TaskStatusUpdateDTO("New name", null);

		mockMvc.perform(put("/api/task_statuses/" + status.getId())
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("New name"))
				.andExpect(jsonPath("$.slug").value("keep-slug"));
	}

	@Test
	void updateRequiresAuth() throws Exception {
		var status = createStatus("Untouchable", "untouchable");
		var data = new TaskStatusUpdateDTO("Touched", null);

		mockMvc.perform(put("/api/task_statuses/" + status.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deleteRemovesStatus() throws Exception {
		var status = createStatus("Delete me", "delete-me");

		mockMvc.perform(delete("/api/task_statuses/" + status.getId()).with(user("someone@example.com")))
				.andExpect(status().isNoContent());

		assertThat(taskStatusRepository.existsById(status.getId())).isFalse();
	}

	@Test
	void deleteRequiresAuth() throws Exception {
		var status = createStatus("Stay forever", "stay-forever");

		mockMvc.perform(delete("/api/task_statuses/" + status.getId()))
				.andExpect(status().isUnauthorized());

		assertThat(taskStatusRepository.existsById(status.getId())).isTrue();
	}

	private TaskStatus createStatus(String name, String slug) {
		var status = new TaskStatus();
		status.setName(name);
		status.setSlug(slug);
		return taskStatusRepository.save(status);
	}

}
