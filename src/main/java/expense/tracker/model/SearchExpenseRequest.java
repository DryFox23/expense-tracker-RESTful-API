package expense.tracker.model;

import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.ExpenseType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchExpenseRequest {

    private String title;
    private BigDecimal amount;
    private ExpenseType expenseType;
    private ExpenseCategory category;

    @NotNull
    private Integer pageNumber;
    @NotNull
    private Integer pageSize;
}
