package yehor.budget.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yehor.budget.entity.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
}
