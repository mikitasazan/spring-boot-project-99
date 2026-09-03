package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;

public record LabelUpdateDTO(
		@Size(min = 3, max = 1000) String name
) {
}
