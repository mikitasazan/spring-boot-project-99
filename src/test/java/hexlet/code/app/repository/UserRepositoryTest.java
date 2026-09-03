package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void savesAndReloadsAUser() {
		var user = new User();
		user.setFirstName("Ada");
		user.setLastName("Lovelace");
		user.setEmail("ada@example.com");
		user.setPassword("secret");

		var saved = userRepository.save(user);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();

		var reloaded = userRepository.findById(saved.getId()).orElseThrow();
		assertThat(reloaded.getEmail()).isEqualTo("ada@example.com");
	}

}
