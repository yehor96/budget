package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.StorageRecord;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StorageRecordRepository extends JpaRepository<StorageRecord, Long> {

    @Query("SELECT s FROM StorageRecord s WHERE s.date BETWEEN :dateFrom AND :dateTo")
    List<StorageRecord> findAllInInterval(@Param("dateFrom") LocalDate dateFrom,
                                          @Param("dateTo") LocalDate dateTo);
}
