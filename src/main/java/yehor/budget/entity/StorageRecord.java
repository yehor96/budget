package yehor.budget.entity;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "storage_records")
public class StorageRecord {

    @Id
    @SequenceGenerator(name = "storage_records_sequence", sequenceName = "storage_records_storage_record_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "storage_records_sequence")
    @Column(name = "storage_record_id")
    private Long id;

    @OneToMany(mappedBy = "storageRecord")
    private List<StorageItem> storageItems;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "total_storage")
    private BigDecimal totalStorage;
}
