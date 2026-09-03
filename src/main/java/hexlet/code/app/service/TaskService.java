package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;
	private final TaskStatusRepository taskStatusRepository;
	private final UserRepository userRepository;

	public List<TaskDTO> getAll() {
		return taskRepository.findAll().stream()
				.map(this::toDTO)
				.toList();
	}

	public TaskDTO getById(Long id) {
		return toDTO(findOrThrow(id));
	}

	public TaskDTO create(TaskCreateDTO data) {
		var task = new Task();
		task.setName(data.title());
		task.setIndex(data.index());
		task.setDescription(data.content());
		task.setTaskStatus(findStatusOrThrow(data.status()));

		if (data.assigneeId() != null) {
			task.setAssignee(findUserOrThrow(data.assigneeId()));
		}

		return toDTO(taskRepository.save(task));
	}

	public TaskDTO update(Long id, TaskUpdateDTO data) {
		var task = findOrThrow(id);

		if (data.title() != null) {
			task.setName(data.title());
		}
		if (data.index() != null) {
			task.setIndex(data.index());
		}
		if (data.content() != null) {
			task.setDescription(data.content());
		}
		if (data.status() != null) {
			task.setTaskStatus(findStatusOrThrow(data.status()));
		}
		if (data.assigneeId() != null) {
			task.setAssignee(findUserOrThrow(data.assigneeId()));
		}

		return toDTO(taskRepository.save(task));
	}

	public void delete(Long id) {
		if (!taskRepository.existsById(id)) {
			throw notFound(id);
		}
		taskRepository.deleteById(id);
	}

	private Task findOrThrow(Long id) {
		return taskRepository.findById(id).orElseThrow(() -> notFound(id));
	}

	private TaskStatus findStatusOrThrow(String slug) {
		return taskStatusRepository.findBySlug(slug)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + slug));
	}

	private User findUserOrThrow(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user id: " + userId));
	}

	private ResponseStatusException notFound(Long id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with id " + id + " not found");
	}

	private TaskDTO toDTO(Task task) {
		return new TaskDTO(
				task.getId(),
				task.getIndex(),
				task.getCreatedAt() == null ? null : task.getCreatedAt().toLocalDate(),
				task.getAssignee() == null ? null : task.getAssignee().getId(),
				task.getName(),
				task.getDescription(),
				task.getTaskStatus().getSlug()
		);
	}

}
