package expense.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "expenses")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Expense {

    @Id
    private String id;
    private String title;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type")
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private ExpensePaymentMethod paymentMethod;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User user;
}
