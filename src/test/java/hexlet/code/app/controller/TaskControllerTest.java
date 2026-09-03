package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class TaskControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;
	private TaskStatus status;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();

		status = new TaskStatus();
		status.setName("Task test status " + System.nanoTime());
		status.setSlug("task-test-status-" + System.nanoTime());
		status = taskStatusRepository.save(status);
	}

	@Test
	void indexWithoutTokenReturns401() throws Exception {
		mockMvc.perform(get("/api/tasks"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createRequiresAuth() throws Exception {
		var data = new TaskCreateDTO(null, null, "Unauthorized task", null, status.getSlug(), null);

		mockMvc.perform(post("/api/tasks")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void createReturnsCreatedTaskWithMappedFields() throws Exception {
		var assignee = createUser("assignee@example.com");
		var data = new TaskCreateDTO(42, assignee.getId(), "Test title", "Test content", status.getSlug(), null);

		mockMvc.perform(post("/api/tasks")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("Test title"))
				.andExpect(jsonPath("$.content").value("Test content"))
				.andExpect(jsonPath("$.index").value(42))
				.andExpect(jsonPath("$.status").value(status.getSlug()))
				.andExpect(jsonPath("$.assignee_id").value(assignee.getId()));
	}

	@Test
	void createWithLabelsRoundTripsTaskLabelIds() throws Exception {
		var label = createLabel("labelled-task-label");
		var data = new TaskCreateDTO(null, null, "Labelled task", null, status.getSlug(), List.of(label.getId()));

		mockMvc.perform(post("/api/tasks")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.taskLabelIds[0]").value(label.getId()));
	}

	@Test
	void createWithUnknownLabelIdReturns400() throws Exception {
		var data = new TaskCreateDTO(null, null, "Title", null, status.getSlug(), List.of(999999L));

		mockMvc.perform(post("/api/tasks")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createWithUnknownStatusReturns400() throws Exception {
		var data = new TaskCreateDTO(null, null, "Title", null, "no-such-slug", null);

		mockMvc.perform(post("/api/tasks")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createWithBlankTitleReturns400() throws Exception {
		var data = new TaskCreateDTO(null, null, "", null, status.getSlug(), null);

		mockMvc.perform(post("/api/tasks")
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void showReturnsOneTask() throws Exception {
		var task = createTask("Show me");

		mockMvc.perform(get("/api/tasks/" + task.getId()).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Show me"));
	}

	@Test
	void showMissingTaskReturns404() throws Exception {
		mockMvc.perform(get("/api/tasks/999999").with(user("someone@example.com")))
				.andExpect(status().isNotFound());
	}

	@Test
	void filterByTitleContReturnsMatchingTasksOnly() throws Exception {
		var marker = "titlecont-" + System.nanoTime();
		createTask(marker + "-match");
		createTask("unrelated-task");

		mockMvc.perform(get("/api/tasks").param("titleCont", marker).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title").value(marker + "-match"));
	}

	@Test
	void filterByAssigneeIdReturnsMatchingTasksOnly() throws Exception {
		var assignee = createUser("filter-assignee-" + System.nanoTime() + "@example.com");
		var task = new Task();
		task.setName("Assigned task for filter");
		task.setTaskStatus(status);
		task.setAssignee(assignee);
		taskRepository.save(task);

		createTask("Unassigned task for filter");

		mockMvc.perform(get("/api/tasks")
						.param("assigneeId", String.valueOf(assignee.getId()))
						.with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].assignee_id").value(assignee.getId()));
	}

	@Test
	void filterByStatusReturnsMatchingTasksOnly() throws Exception {
		createTask("Task with this status");

		var otherStatus = new TaskStatus();
		otherStatus.setName("Other status " + System.nanoTime());
		otherStatus.setSlug("other-status-" + System.nanoTime());
		taskStatusRepository.save(otherStatus);

		var otherTask = new Task();
		otherTask.setName("Task with other status");
		otherTask.setTaskStatus(otherStatus);
		taskRepository.save(otherTask);

		mockMvc.perform(get("/api/tasks").param("status", status.getSlug()).with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].status").value(status.getSlug()));
	}

	@Test
	void filterByLabelIdReturnsMatchingTasksOnly() throws Exception {
		var label = createLabel("filter-label-" + System.nanoTime());
		var task = new Task();
		task.setName("Labelled for filter");
		task.setTaskStatus(status);
		task.setLabels(java.util.Set.of(label));
		taskRepository.save(task);

		createTask("Not labelled for filter");

		mockMvc.perform(get("/api/tasks")
						.param("labelId", String.valueOf(label.getId()))
						.with(user("someone@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].taskLabelIds[0]").value(label.getId()));
	}

	@Test
	void updateChangesOnlySentFields() throws Exception {
		var task = createTask("Old title");
		var data = new TaskUpdateDTO(null, null, "New title", null, null, null);

		mockMvc.perform(put("/api/tasks/" + task.getId())
						.with(user("someone@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("New title"))
				.andExpect(jsonPath("$.status").value(status.getSlug()));
	}

	@Test
	void updateRequiresAuth() throws Exception {
		var task = createTask("Untouchable");
		var data = new TaskUpdateDTO(null, null, "Touched", null, null, null);

		mockMvc.perform(put("/api/tasks/" + task.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(data)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deleteRemovesTask() throws Exception {
		var task = createTask("Delete me");

		mockMvc.perform(delete("/api/tasks/" + task.getId()).with(user("someone@example.com")))
				.andExpect(status().isNoContent());

		assertThat(taskRepository.existsById(task.getId())).isFalse();
	}

	@Test
	void deleteRequiresAuth() throws Exception {
		var task = createTask("Stay forever");

		mockMvc.perform(delete("/api/tasks/" + task.getId()))
				.andExpect(status().isUnauthorized());

		assertThat(taskRepository.existsById(task.getId())).isTrue();
	}

	private Task createTask(String title) {
		var task = new Task();
		task.setName(title);
		task.setTaskStatus(status);
		return taskRepository.save(task);
	}

	private User createUser(String email) {
		var savedUser = new User();
		savedUser.setEmail(email);
		savedUser.setPassword("irrelevant-for-this-test");
		return userRepository.save(savedUser);
	}

	private Label createLabel(String name) {
		var label = new Label();
		label.setName(name);
		return labelRepository.save(label);
	}

}
