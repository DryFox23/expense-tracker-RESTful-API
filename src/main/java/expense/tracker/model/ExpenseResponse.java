package expense.tracker.model;

import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.ExpensePaymentMethod;
import expense.tracker.entity.ExpenseType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseResponse {

    @Id
    private String id;
    @NotNull
    private String title;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private ExpenseType type;
    private ExpenseCategory category;
    private ExpensePaymentMethod paymentMethod;
}
