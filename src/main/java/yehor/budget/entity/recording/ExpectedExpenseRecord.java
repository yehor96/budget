package yehor.budget.entity.recording;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@ToString
@Entity
@Table(name = "expected_expense_records")
public class ExpectedExpenseRecord {

    @Id
    @SequenceGenerator(name = "expected_expense_records_sequence", sequenceName = "expected_expense_records_expected_expense_record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expected_expense_records_sequence")
    @Column(name = "expected_expense_record_id")
    private Long id;

    @Column(name = "total_expected_expenses_days_1_7")
    private BigDecimal total1to7;

    @Column(name = "total_expected_expenses_days_8_14")
    private BigDecimal total8to14;

    @Column(name = "total_expected_expenses_days_15_21")
    private BigDecimal total15to21;

    @Column(name = "total_expected_expenses_days_22_31")
    private BigDecimal total22to31;

    @JoinColumn(name = "balance_record_id", nullable = false)
    @ToString.Exclude
    @OneToOne
    private BalanceRecord balanceRecord;
}
