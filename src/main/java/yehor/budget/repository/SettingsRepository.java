package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yehor.budget.entity.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
