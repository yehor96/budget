package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yehor.budget.entity.FutureExpense;

import java.time.LocalDate;
import java.util.List;

public interface FutureExpenseRepository extends JpaRepository<FutureExpense, Long> {
    @Query("SELECT e FROM FutureExpense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    List<FutureExpense> getFutureExpensesInInterval(@Param("dateFrom") LocalDate dateFrom,
                                                 @Param("dateTo") LocalDate dateTo);
}
