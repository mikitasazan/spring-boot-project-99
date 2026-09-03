package hexlet.code.app.component;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskStatusInitializerTest {

	@Autowired
	private TaskStatusInitializer taskStatusInitializer;

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Test
	void seedsTheDefaultStatusesOnceAndIsIdempotentOnRerun() {
		var slugs = new String[] {"draft", "to_review", "to_be_fixed", "to_publish", "published"};

		for (var slug : slugs) {
			assertThat(taskStatusRepository.findBySlug(slug)).isPresent();
		}

		taskStatusInitializer.run((ApplicationArguments) null);

		for (var slug : slugs) {
			var matching = taskStatusRepository.findAll().stream()
					.filter(status -> slug.equals(status.getSlug()))
					.count();
			assertThat(matching).isEqualTo(1);
		}
	}

}
