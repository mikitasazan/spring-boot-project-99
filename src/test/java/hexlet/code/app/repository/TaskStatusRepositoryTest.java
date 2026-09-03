package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class TaskStatusRepositoryTest {

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Test
	void savesAndFindsBySlug() {
		var status = new TaskStatus();
		status.setName("Draft");
		status.setSlug("draft");
		taskStatusRepository.save(status);

		var found = taskStatusRepository.findBySlug("draft").orElseThrow();
		assertThat(found.getName()).isEqualTo("Draft");
	}

	@Test
	void findBySlugReturnsEmptyWhenMissing() {
		assertThat(taskStatusRepository.findBySlug("missing")).isEmpty();
	}

}
