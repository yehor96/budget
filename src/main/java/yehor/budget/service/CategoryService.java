package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import yehor.budget.entity.Category;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger LOG = LogManager.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    public List<CategoryFullDto> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryConverter::convert)
                .toList();
    }

    public void save(CategoryLimitedDto categoryDto) {
        Category category = categoryConverter.convert(categoryDto);
        validateNotExists(category);
        categoryRepository.save(category);
        LOG.info("{} is saved", category);
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
            LOG.info("Category with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Category with id " + id + " not found");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Cannot delete category with dependent expenses");
        }
    }

    @Transactional
    public void update(CategoryFullDto categoryDto) {
        validateExists(categoryDto.getId());
        Category category = categoryConverter.convert(categoryDto);
        categoryRepository.save(category);
        LOG.info("{} is updated", category);
    }

    private void validateNotExists(Category category) {
        categoryRepository.findByName(category.getName())
                .ifPresent(e -> {
                    throw new ObjectAlreadyExistsException("Category " + category.getName() + " already exists");
                });
    }

    private void validateExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ObjectNotFoundException("Category with id " + id + " does not exist");
        }
    }
}
