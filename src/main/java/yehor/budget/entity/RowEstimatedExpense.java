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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "row_estimated_expenses")
public class RowEstimatedExpense {

    @Id
    @SequenceGenerator(
            name = "row_estimated_expenses_sequence",
            sequenceName = "row_estimated_expenses_row_estimated_expense_id_seq",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "row_estimated_expenses_sequence")
    @Column(name = "row_estimated_expense_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "days_1_to_7")
    private BigDecimal days1to7;

    @Column(name = "days_8_to_14")
    private BigDecimal days8to14;

    @Column(name = "days_15_to_21")
    private BigDecimal days15to21;

    @Column(name = "days_22_to_31")
    private BigDecimal days22to31;

    @Override
    public String toString() {
        return "RowEstimatedExpense{" +
                "categoryId=" + category.getId() +
                ", days1to7=" + days1to7 +
                ", days8to14=" + days8to14 +
                ", days15to21=" + days15to21 +
                ", days22to31=" + days22to31 +
                '}';
    }
}
