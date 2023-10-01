package yehor.budget.repository.recording;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.recording.ExpectedExpenseRecord;

@Repository
public interface ExpectedExpenseRecordRepository extends JpaRepository<ExpectedExpenseRecord, Long> {
}
