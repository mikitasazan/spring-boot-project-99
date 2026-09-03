package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Partial update: a null field means "not sent, leave unchanged".
public record UserUpdateDTO(
		@Email String email,
		String firstName,
		String lastName,
		@Size(min = 3) String password
) {
}
