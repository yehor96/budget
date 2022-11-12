package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import yehor.budget.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    boolean existsByName(@Param("name") String name);
}
