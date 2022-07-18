package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import yehor.budget.entity.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Modifying
    @Query("UPDATE Settings s " +
            "SET s.isBudgetDateValidation = :#{#settings.isBudgetDateValidation}, " +
            "s.budgetStartDate = :#{#settings.budgetStartDate}, " +
            "s.budgetEndDate = :#{#settings.budgetEndDate} " +
            "WHERE s.id = :#{#settings.id}")
    void updateById(Settings settings);
}
