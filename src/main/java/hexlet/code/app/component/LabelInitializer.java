package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LabelInitializer implements ApplicationRunner {

	private static final List<String> DEFAULT_LABELS = List.of("feature", "bug");

	private final LabelRepository labelRepository;

	@Override
	public void run(ApplicationArguments args) {
		DEFAULT_LABELS.stream()
				.filter(name -> labelRepository.findByName(name).isEmpty())
				.forEach(name -> {
					var label = new Label();
					label.setName(name);
					labelRepository.save(label);
				});
	}

}
