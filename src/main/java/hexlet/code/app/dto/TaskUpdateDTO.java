package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskUpdateDTO(
		Integer index,
		@JsonProperty("assignee_id") Long assigneeId,
		String title,
		String content,
		String status
) {
}
