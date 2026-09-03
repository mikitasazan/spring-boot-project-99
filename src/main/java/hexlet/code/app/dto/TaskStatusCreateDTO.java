package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskStatusCreateDTO(
		@NotBlank String name,
		@NotBlank String slug
) {
}
