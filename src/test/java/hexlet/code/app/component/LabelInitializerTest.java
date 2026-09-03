package hexlet.code.app.component;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.repository.LabelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LabelInitializerTest {

	@Autowired
	private LabelInitializer labelInitializer;

	@Autowired
	private LabelRepository labelRepository;

	@Test
	void seedsTheDefaultLabelsOnceAndIsIdempotentOnRerun() {
		var names = new String[] {"feature", "bug"};

		labelInitializer.run((ApplicationArguments) null);

		for (var name : names) {
			assertThat(labelRepository.findByName(name)).isPresent();
		}

		labelInitializer.run((ApplicationArguments) null);

		for (var name : names) {
			var matching = labelRepository.findAll().stream()
					.filter(label -> name.equals(label.getName()))
					.count();
			assertThat(matching).isEqualTo(1);
		}
	}

}
