package hexlet.code.app.component;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskStatusInitializer implements ApplicationRunner {

	private static final Map<String, String> DEFAULT_STATUSES = new LinkedHashMap<>();

	static {
		DEFAULT_STATUSES.put("draft", "Draft");
		DEFAULT_STATUSES.put("to_review", "ToReview");
		DEFAULT_STATUSES.put("to_be_fixed", "ToBeFixed");
		DEFAULT_STATUSES.put("to_publish", "ToPublish");
		DEFAULT_STATUSES.put("published", "Published");
	}

	private final TaskStatusRepository taskStatusRepository;

	@Override
	public void run(ApplicationArguments args) {
		DEFAULT_STATUSES.entrySet().stream()
				.filter(entry -> taskStatusRepository.findBySlug(entry.getKey()).isEmpty())
				.forEach(entry -> {
					var status = new TaskStatus();
					status.setSlug(entry.getKey());
					status.setName(entry.getValue());
					taskStatusRepository.save(status);
				});
	}

}
