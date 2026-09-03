package hexlet.code.app.config;

import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

	public SentryConfig(
			@Value("${sentry.dsn:}") String dsn,
			@Value("${sentry.environment:dev}") String environment) {
		if (dsn.isBlank()) {
			return;
		}

		Sentry.init(options -> {
			options.setDsn(dsn);
			options.setEnvironment(environment);
		});
	}

}
