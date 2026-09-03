package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record UserDTO(
		Long id,
		String email,
		String firstName,
		String lastName,
		@JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdAt
) {
}
