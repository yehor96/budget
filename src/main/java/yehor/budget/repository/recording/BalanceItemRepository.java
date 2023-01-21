package yehor.budget.repository.recording;

import org.springframework.data.jpa.repository.JpaRepository;
import yehor.budget.entity.recording.BalanceItem;

public interface BalanceItemRepository extends JpaRepository<BalanceItem, Long> {
}
