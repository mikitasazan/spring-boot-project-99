package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class TaskStatusController {

	private final TaskStatusService taskStatusService;

	@GetMapping
	public ResponseEntity<List<TaskStatusDTO>> index() {
		var statuses = taskStatusService.getAll();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(statuses.size()))
				.body(statuses);
	}

	@GetMapping("/{id}")
	public TaskStatusDTO show(@PathVariable Long id) {
		return taskStatusService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO data) {
		return taskStatusService.create(data);
	}

	@PutMapping("/{id}")
	public TaskStatusDTO update(@PathVariable Long id, @RequestBody TaskStatusUpdateDTO data) {
		return taskStatusService.update(id, data);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable Long id) {
		taskStatusService.delete(id);
	}

}
