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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "income_sources")
public class IncomeSource {

    @Id
    @SequenceGenerator(name = "income_sources_sequence", sequenceName = "income_sources_income_source_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "income_sources_sequence")
    @Column(name = "income_source_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private BigDecimal value;
}
