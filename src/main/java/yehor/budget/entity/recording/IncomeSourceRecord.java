package yehor.budget.entity.recording;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import yehor.budget.common.Currency;
import yehor.budget.service.client.currency.Exchangeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "income_source_records")
public class IncomeSourceRecord implements Exchangeable {

    @Id
    @SequenceGenerator(name = "income_source_records_sequence", sequenceName = "income_source_records_income_source_record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_source_records_sequence")
    @Column(name = "income_source_record_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "accrual_day")
    private Integer accrualDay;

    @JoinColumn(name = "balance_record_id", nullable = false)
    @ToString.Exclude
    @ManyToOne
    private BalanceRecord balanceRecord;
}
