package yehor.budget.entity;

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
@Table(name = "row_regular_expected_expenses")
public class RowRegularExpectedExpense {

    @Id
    @SequenceGenerator(
            name = "row_regular_expected_expenses_sequence",
            sequenceName = "row_regular_expected_expenses_row_regular_expected_expenses_id_seq",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "row_regular_expected_expenses_sequence")
    @Column(name = "row_regular_expected_expenses_id")
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
}
