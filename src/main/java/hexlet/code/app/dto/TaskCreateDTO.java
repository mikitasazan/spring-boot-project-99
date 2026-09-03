package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record TaskCreateDTO(
		Integer index,
		@JsonProperty("assignee_id") Long assigneeId,
		@NotBlank String title,
		String content,
		@NotBlank String status,
		List<Long> taskLabelIds
) {
}
