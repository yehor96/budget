package yehor.budget.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import yehor.budget.entity.Settings;

public interface SettingsRepository extends CrudRepository<Settings, Long> {

    @Modifying
    @Query("UPDATE Settings s " +
            "SET s.isBudgetDateValidation = :#{#settings.isBudgetDateValidation} " +
            "WHERE s.id = :#{#settings.id}")
    void saveById(Settings settings);
}
