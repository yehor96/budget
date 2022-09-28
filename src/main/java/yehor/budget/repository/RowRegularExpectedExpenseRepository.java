package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.RowRegularExpectedExpense;

@Repository
public interface RowRegularExpectedExpenseRepository extends JpaRepository<RowRegularExpectedExpense, Long> {
}
