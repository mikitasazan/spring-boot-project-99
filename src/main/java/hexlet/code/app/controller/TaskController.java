package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.service.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	public ResponseEntity<List<TaskDTO>> index(
			@RequestParam(required = false) String titleCont,
			@RequestParam(required = false) Long assigneeId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) Long labelId
	) {
		var tasks = taskService.getAll(titleCont, assigneeId, status, labelId);
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(tasks.size()))
				.body(tasks);
	}

	@GetMapping("/{id}")
	public TaskDTO show(@PathVariable Long id) {
		return taskService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskDTO create(@Valid @RequestBody TaskCreateDTO data) {
		return taskService.create(data);
	}

	@PutMapping("/{id}")
	public TaskDTO update(@PathVariable Long id, @RequestBody TaskUpdateDTO data) {
		return taskService.update(id, data);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable Long id) {
		taskService.delete(id);
	}

}
