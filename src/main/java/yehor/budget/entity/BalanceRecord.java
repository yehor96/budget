package yehor.budget.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonManagedReference
    private List<BalanceItem> balanceItems;
}
