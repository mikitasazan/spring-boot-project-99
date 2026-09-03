package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<UserDTO>> index() {
		var users = userService.getAll();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(users.size()))
				.body(users);
	}

	@GetMapping("/{id}")
	public UserDTO show(@PathVariable Long id) {
		return userService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDTO create(@Valid @RequestBody UserCreateDTO data) {
		return userService.create(data);
	}

	@PutMapping("/{id}")
	public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
		return userService.update(id, data);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable Long id) {
		userService.delete(id);
	}

}
