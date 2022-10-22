package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.entity.RowEstimatedExpense;

@Repository
public interface RowEstimatedExpenseRepository extends JpaRepository<RowEstimatedExpense, Long> {

    boolean existsByCategoryId(Long categoryId);

    @Transactional
    @Modifying
    @Query("UPDATE RowEstimatedExpense r " +
            "SET r.days1to7 = :#{#row.days1to7}, " +
            "r.days8to14 = :#{#row.days8to14}, " +
            "r.days15to21 = :#{#row.days15to21}, " +
            "r.days22to31 = :#{#row.days22to31} " +
            "WHERE r.category.id = :#{#row.category.id}")
    void updateByCategoryId(@Param("row") RowEstimatedExpense rowEstimatedExpense);
}
