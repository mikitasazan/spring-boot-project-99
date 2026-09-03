package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.model.Label;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class LabelRepositoryTest {

	@Autowired
	private LabelRepository labelRepository;

	@Test
	void savesAndFindsByName() {
		var label = new Label();
		label.setName("regression");
		labelRepository.save(label);

		var found = labelRepository.findByName("regression").orElseThrow();
		assertThat(found.getName()).isEqualTo("regression");
	}

	@Test
	void findByNameReturnsEmptyWhenMissing() {
		assertThat(labelRepository.findByName("missing")).isEmpty();
	}

}
