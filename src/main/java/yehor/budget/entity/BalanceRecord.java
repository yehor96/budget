package yehor.budget.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Data
@Table(name = "balance_records")
public class BalanceRecord {

    @Id
    @SequenceGenerator(name = "balance_records_sequence", sequenceName = "balance_records_balance_record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_records_sequence")
    @Column(name = "balance_record_id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(mappedBy = "balanceRecord")
    private List<BalanceItem> balanceItems;

    @Column(name = "total")
    private BigDecimal total;
}
