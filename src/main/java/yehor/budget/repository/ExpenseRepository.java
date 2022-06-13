package yehor.budget.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.Expense;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    int findSumInInterval(@Param("dateFrom") LocalDate dateFrom,
                          @Param("dateTo") LocalDate dateTo);

    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    List<Expense> findAllInInterval(@Param("dateFrom") LocalDate dateFrom,
                                    @Param("dateTo") LocalDate dateTo);

    @Modifying
    @Query("UPDATE Expense e " +
            "SET e.value = :#{#expense.value}, " +
            "e.isRegular = :#{#expense.isRegular}, " +
            "e.date = :#{#expense.date} " +
            "WHERE e.id = :#{#expense.id}")
    void updateById(@Param("expense") Expense expense);
}
