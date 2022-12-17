package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yehor.budget.entity.BalanceRecord;

public interface BalanceRecordRepository extends JpaRepository<BalanceRecord, Long> {
}
