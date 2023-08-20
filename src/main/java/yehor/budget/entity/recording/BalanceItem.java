package yehor.budget.entity.recording;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "balance_items")
public class BalanceItem {

    @Id
    @SequenceGenerator(name = "balance_items_sequence", sequenceName = "balance_items_balance_item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_items_sequence")
    @Column(name = "balance_item_id")
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "balance_record_id", nullable = false)
    @ToString.Exclude
    private BalanceRecord balanceRecord;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "card")
    private BigDecimal card;
}
