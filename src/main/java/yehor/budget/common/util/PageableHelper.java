package yehor.budget.common.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PageableHelper {

    public <T> Optional<T> getLatestByDate(JpaRepository<T, Long> repository) {
        return getLatestByColumn(repository, "date");
    }

    public <T> Optional<T> getLatestByColumn(JpaRepository<T, Long> repository, String columnName) {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, columnName);
        Page<T> results = repository.findAll(pageable);
        return results.isEmpty() ? Optional.empty() : Optional.ofNullable(results.toList().get(0));
    }
}
