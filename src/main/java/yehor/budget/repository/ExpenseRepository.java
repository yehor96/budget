package yehor.budget.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.DailyExpense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends CrudRepository<DailyExpense, Long> {

    @Query("SELECT e FROM DailyExpense e WHERE e.date = :date")
    Optional<DailyExpense> findByDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(e.value) FROM DailyExpense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    int findSumInInterval(@Param("dateFrom") LocalDate dateFrom,
                          @Param("dateTo") LocalDate dateTo);

    @Query("SELECT e FROM DailyExpense e WHERE e.date BETWEEN :dateFrom AND :dateTo")
    List<DailyExpense> findAllInInterval(@Param("dateFrom") LocalDate dateFrom,
                                         @Param("dateTo") LocalDate dateTo);


    @Modifying
    @Query("UPDATE DailyExpense e " +
            "SET e.value = :#{#expense.value}, " +
            "e.isRegular = :#{#expense.isRegular} " +
            "WHERE e.date = :#{#expense.date}")
    void updateByDate(@Param("expense") DailyExpense dailyExpense);
}
