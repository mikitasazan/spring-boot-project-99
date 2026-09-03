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
import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
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
class LabelControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private TaskRepository taskRepository;

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
		mockMvc.perform(get("/api/labels"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createRequiresAuth() throws Exception {
		var data = new LabelCreateDTO("unauthorized");

		mockMvc.perform(post("/api/labels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createReturnsCreatedLabel() throws Exception {
		var data = new LabelCreateDTO("urgent");

		mockMvc.perform(post("/api/labels")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("urgent"));

		assertThat(labelRepository.findByName("urgent")).isPresent();
	}

	@Test
	void createWithTooShortNameReturns400() throws Exception {
		var data = new LabelCreateDTO("ab");

		mockMvc.perform(post("/api/labels")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void showReturnsOneLabel() throws Exception {
		var label = createLabel("show-me-label");

		mockMvc.perform(get("/api/labels/" + label.getId()).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("show-me-label"));
	}

	@Test
	void showMissingLabelReturns404() throws Exception {
		mockMvc.perform(get("/api/labels/999999").with(user("someone@example.com")))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateChangesName() throws Exception {
		var label = createLabel("old-name-label");
		var data = new LabelUpdateDTO("new-name-label");

		mockMvc.perform(put("/api/labels/" + label.getId())
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("new-name-label"));
	}

	@Test
	void updateRequiresAuth() throws Exception {
		var label = createLabel("untouchable-label");
		var data = new LabelUpdateDTO("touched-label");

		mockMvc.perform(put("/api/labels/" + label.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deleteRemovesLabel() throws Exception {
		var label = createLabel("delete-me-label");

		mockMvc.perform(delete("/api/labels/" + label.getId()).with(user("someone@example.com")))
				.andExpect(status().isNoContent());

		assertThat(labelRepository.existsById(label.getId())).isFalse();
	}

	@Test
	void deleteRequiresAuth() throws Exception {
		var label = createLabel("stay-forever-label");

		mockMvc.perform(delete("/api/labels/" + label.getId()))
				.andExpect(status().isUnauthorized());

		assertThat(labelRepository.existsById(label.getId())).isTrue();
	}

	@Test
	void cannotDeleteLabelAssignedToTask() throws Exception {
		var label = createLabel("blocking-label");

		var status = new TaskStatus();
		status.setName("Blocking status for label test");
		status.setSlug("blocking-status-for-label-test");
		taskStatusRepository.save(status);

		var task = new Task();
		task.setName("Task with label");
		task.setTaskStatus(status);
		task.setLabels(java.util.Set.of(label));
		taskRepository.save(task);

		mockMvc.perform(delete("/api/labels/" + label.getId()).with(user("someone@example.com")))
				.andExpect(status().isConflict());

		assertThat(labelRepository.existsById(label.getId())).isTrue();
	}

	private Label createLabel(String name) {
		var label = new Label();
		label.setName(name);
		return labelRepository.save(label);
	}

}
