package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.IncomeSource;

@Repository
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long> {

    boolean existsByName(@Param("name") String name);
}
