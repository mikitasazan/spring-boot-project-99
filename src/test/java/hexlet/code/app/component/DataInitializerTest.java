package hexlet.code.app.component;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class DataInitializerTest {

	@Autowired
	private DataInitializer dataInitializer;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void createsTheAdminOnceAndIsIdempotentOnRerun() {
		dataInitializer.run((ApplicationArguments) null);

		var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();
		assertThat(passwordEncoder.matches("qwerty", admin.getPassword())).isTrue();

		dataInitializer.run((ApplicationArguments) null);

		var admins = userRepository.findAll().stream()
				.filter(u -> "hexlet@example.com".equals(u.getEmail()))
				.count();
		assertThat(admins).isEqualTo(1);
	}

}
