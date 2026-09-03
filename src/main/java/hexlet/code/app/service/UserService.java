package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public List<UserDTO> getAll() {
		return userRepository.findAll().stream()
				.map(this::toDTO)
				.toList();
	}

	public UserDTO getById(Long id) {
		return toDTO(findOrThrow(id));
	}

	public UserDTO create(UserCreateDTO data) {
		var user = new User();
		user.setEmail(data.email());
		user.setFirstName(data.firstName());
		user.setLastName(data.lastName());
		user.setPassword(passwordEncoder.encode(data.password()));

		return toDTO(userRepository.save(user));
	}

	public UserDTO update(Long id, UserUpdateDTO data) {
		var user = findOrThrow(id);

		if (data.email() != null) {
			user.setEmail(data.email());
		}
		if (data.firstName() != null) {
			user.setFirstName(data.firstName());
		}
		if (data.lastName() != null) {
			user.setLastName(data.lastName());
		}
		if (data.password() != null) {
			user.setPassword(passwordEncoder.encode(data.password()));
		}

		return toDTO(userRepository.save(user));
	}

	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw notFound(id);
		}
		userRepository.deleteById(id);
	}

	private User findOrThrow(Long id) {
		return userRepository.findById(id).orElseThrow(() -> notFound(id));
	}

	private ResponseStatusException notFound(Long id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
	}

	private UserDTO toDTO(User user) {
		return new UserDTO(
				user.getId(),
				user.getEmail(),
				user.getFirstName(),
				user.getLastName(),
				user.getCreatedAt() == null ? null : user.getCreatedAt().toLocalDate()
		);
	}

}
