package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yehor.budget.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.name = :name")
    Optional<Tag> findByName(@Param("name") String name);

    @Modifying
    @Query("UPDATE Tag t " +
            "SET t.name = :#{#tag.name} " +
            "WHERE t.id = :#{#tag.id}")
    void update(@Param("tag") Tag tag);
}
