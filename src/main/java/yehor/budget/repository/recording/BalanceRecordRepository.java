package yehor.budget.repository.recording;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yehor.budget.entity.recording.BalanceRecord;

import java.time.LocalDate;
import java.util.List;

public interface BalanceRecordRepository extends JpaRepository<BalanceRecord, Long> {

    @Query("SELECT b FROM BalanceRecord b WHERE b.date BETWEEN :dateFrom AND :dateTo")
    List<BalanceRecord> findAllInInterval(@Param("dateFrom") LocalDate dateFrom,
                                          @Param("dateTo") LocalDate dateTo);
}
