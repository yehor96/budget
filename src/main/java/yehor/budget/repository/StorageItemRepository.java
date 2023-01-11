package yehor.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.StorageItem;

@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {
}
