package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    BigDecimal findSumInInterval(@Param("dateFrom") LocalDate dateFrom,
                                 @Param("dateTo") LocalDate dateTo);

    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    List<Expense> findAllInInterval(@Param("dateFrom") LocalDate dateFrom,
                                    @Param("dateTo") LocalDate dateTo);
}
