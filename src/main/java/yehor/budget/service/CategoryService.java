package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Category;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

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
        log.info("Saved: {}", category);
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
            log.info("Category with id {} is deleted", id);
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
        log.info("Updated: {}", category);
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
