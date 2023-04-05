package yehor.budget.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "future_expenses")
public class FutureExpense {

    @Id
    @SequenceGenerator(name = "future_expenses_sequence", sequenceName = "future_expenses_future_expense_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "future_expenses_sequence")
    @Column(name = "future_expense_id")
    private Long id;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "date")
    private LocalDate date;
}
