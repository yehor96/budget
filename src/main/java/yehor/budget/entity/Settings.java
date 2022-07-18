package yehor.budget.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Proxy(lazy = false)
@Table(name = "settings")
public class Settings {

    @Id
    @SequenceGenerator(name = "settings_sequence", sequenceName = "settings_settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings_sequence")
    @Column(name = "settings_id")
    private Long id;

    @Column(name = "budget_start_date")
    private LocalDate budgetStartDate;

    @Column(name = "budget_end_date")
    private LocalDate budgetEndDate;

    @Column(name = "budget_date_validation")
    private Boolean isBudgetDateValidation;
}
