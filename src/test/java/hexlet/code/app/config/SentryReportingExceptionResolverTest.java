package hexlet.code.app.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

class SentryReportingExceptionResolverTest {

	private final SentryReportingExceptionResolver resolver = new SentryReportingExceptionResolver();

	@Test
	void reportsUnexpectedExceptionsWithoutCrashingWhenSentryIsNotInitialized() {
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();

		assertThatCode(() -> {
			var result = resolver.resolveException(request, response, new Object(), new RuntimeException("boom"));
			assertThat(result).isNull();
		}).doesNotThrowAnyException();
	}

	@Test
	void doesNotDoubleReportExpectedResponseStatusExceptions() {
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();
		var expected = new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "not found");

		var result = resolver.resolveException(request, response, new Object(), expected);

		assertThat(result).isNull();
	}

}
