package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yehor.budget.entity.BalanceItem;

public interface BalanceItemRepository extends JpaRepository<BalanceItem, Long> {
}
