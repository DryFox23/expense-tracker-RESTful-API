package expense.tracker.repository;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {

    Optional<Expense> findFirstByUserAndId(User user, String expenseId);
}
