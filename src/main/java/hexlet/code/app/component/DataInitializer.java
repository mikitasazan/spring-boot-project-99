package hexlet.code.app.component;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

	private static final String ADMIN_EMAIL = "hexlet@example.com";
	private static final String ADMIN_PASSWORD = "qwerty";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) {
		if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
			return;
		}

		var admin = new User();
		admin.setEmail(ADMIN_EMAIL);
		admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
		userRepository.save(admin);
	}

}
