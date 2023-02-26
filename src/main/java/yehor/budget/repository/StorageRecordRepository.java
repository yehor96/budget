package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.StorageRecord;

import java.time.LocalDate;

@Repository
public interface StorageRecordRepository extends JpaRepository<StorageRecord, Long> {
    boolean existsByDate(LocalDate date);
}
