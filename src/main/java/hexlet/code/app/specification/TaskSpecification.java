package hexlet.code.app.specification;

import hexlet.code.app.model.Task;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecification {

	private TaskSpecification() {
	}

	public static Specification<Task> withFilter(String titleCont, Long assigneeId, String status, Long labelId) {
		return (root, query, cb) -> {
			var predicates = new ArrayList<Predicate>();

			if (titleCont != null && !titleCont.isBlank()) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%"));
			}
			if (assigneeId != null) {
				predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
			}
			if (status != null && !status.isBlank()) {
				predicates.add(cb.equal(root.get("taskStatus").get("slug"), status));
			}
			if (labelId != null) {
				predicates.add(cb.equal(root.join("labels").get("id"), labelId));
			}

			query.distinct(true);
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
