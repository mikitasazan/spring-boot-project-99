package hexlet.code.app.dto;

import java.time.LocalDate;

public record LabelDTO(Long id, String name, LocalDate createdAt) {
}
