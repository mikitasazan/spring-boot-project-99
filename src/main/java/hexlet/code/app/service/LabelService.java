package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LabelService {

	private final LabelRepository labelRepository;
	private final TaskRepository taskRepository;

	public List<LabelDTO> getAll() {
		return labelRepository.findAll().stream()
				.map(this::toDTO)
				.toList();
	}

	public LabelDTO getById(Long id) {
		return toDTO(findOrThrow(id));
	}

	public LabelDTO create(LabelCreateDTO data) {
		var label = new Label();
		label.setName(data.name());

		return toDTO(labelRepository.save(label));
	}

	public LabelDTO update(Long id, LabelUpdateDTO data) {
		var label = findOrThrow(id);

		if (data.name() != null) {
			label.setName(data.name());
		}

		return toDTO(labelRepository.save(label));
	}

	public void delete(Long id) {
		var label = findOrThrow(id);

		if (taskRepository.existsByLabelsContaining(label)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete a label assigned to existing tasks");
		}

		labelRepository.deleteById(id);
	}

	private Label findOrThrow(Long id) {
		return labelRepository.findById(id).orElseThrow(() -> notFound(id));
	}

	private ResponseStatusException notFound(Long id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Label with id " + id + " not found");
	}

	private LabelDTO toDTO(Label label) {
		return new LabelDTO(
				label.getId(),
				label.getName(),
				label.getCreatedAt() == null ? null : label.getCreatedAt().toLocalDate()
		);
	}

}
