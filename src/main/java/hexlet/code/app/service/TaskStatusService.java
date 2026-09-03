package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

	private final TaskStatusRepository taskStatusRepository;
	private final TaskRepository taskRepository;

	public List<TaskStatusDTO> getAll() {
		return taskStatusRepository.findAll().stream()
				.map(this::toDTO)
				.toList();
	}

	public TaskStatusDTO getById(Long id) {
		return toDTO(findOrThrow(id));
	}

	public TaskStatusDTO create(TaskStatusCreateDTO data) {
		var status = new TaskStatus();
		status.setName(data.name());
		status.setSlug(data.slug());

		return toDTO(taskStatusRepository.save(status));
	}

	public TaskStatusDTO update(Long id, TaskStatusUpdateDTO data) {
		var status = findOrThrow(id);

		if (data.name() != null) {
			status.setName(data.name());
		}
		if (data.slug() != null) {
			status.setSlug(data.slug());
		}

		return toDTO(taskStatusRepository.save(status));
	}

	public void delete(Long id) {
		var status = findOrThrow(id);

		if (taskRepository.existsByTaskStatus(status)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete a status assigned to existing tasks");
		}

		taskStatusRepository.deleteById(id);
	}

	private TaskStatus findOrThrow(Long id) {
		return taskStatusRepository.findById(id).orElseThrow(() -> notFound(id));
	}

	private ResponseStatusException notFound(Long id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status with id " + id + " not found");
	}

	private TaskStatusDTO toDTO(TaskStatus status) {
		return new TaskStatusDTO(
				status.getId(),
				status.getName(),
				status.getSlug(),
				status.getCreatedAt() == null ? null : status.getCreatedAt().toLocalDate()
		);
	}

}
