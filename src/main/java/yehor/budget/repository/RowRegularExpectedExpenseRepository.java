package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yehor.budget.entity.RowRegularExpectedExpense;

public interface RowRegularExpectedExpenseRepository extends JpaRepository<RowRegularExpectedExpense, Long> {
}
