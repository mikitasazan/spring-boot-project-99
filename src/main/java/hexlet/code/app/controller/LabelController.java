package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
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
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

	private final LabelService labelService;

	@GetMapping
	public ResponseEntity<List<LabelDTO>> index() {
		var labels = labelService.getAll();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(labels.size()))
				.body(labels);
	}

	@GetMapping("/{id}")
	public LabelDTO show(@PathVariable Long id) {
		return labelService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
		return labelService.create(data);
	}

	@PutMapping("/{id}")
	public LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO data) {
		return labelService.update(id, data);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable Long id) {
		labelService.delete(id);
	}

}
