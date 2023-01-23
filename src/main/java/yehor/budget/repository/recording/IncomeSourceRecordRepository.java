package yehor.budget.repository.recording;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.recording.IncomeSourceRecord;

@Repository
public interface IncomeSourceRecordRepository extends JpaRepository<IncomeSourceRecord, Long> {
}
