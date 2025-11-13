package expense.tracker.specification;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import expense.tracker.model.SearchExpenseRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseSpecification {

    private ExpenseSpecification() {
    }

    public static Specification<Expense> filterExpense(SearchExpenseRequest request, User user) {
        return ((root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("user"), user));

            if (StringUtils.hasText(request.getTitle())) {
                String titlePattern = "%" + request.getTitle().toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get("title")), titlePattern));
            }

            Optional.ofNullable(request.getExpenseType())
                    .ifPresent(type -> predicates.add(builder.equal(root.get("expenseType"), type)));

            Optional.ofNullable(request.getCategory())
                    .ifPresent(category -> predicates.add(builder.equal(root.get("category"), category)));

            Optional.ofNullable(request.getAmount())
                    .ifPresent(amount -> predicates.add(builder.equal(root.get("amount"), amount)));

            return builder.and(predicates.toArray(Predicate[]::new));
        });
    }
}
