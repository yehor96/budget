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
import yehor.budget.web.dto.limited.CategoryLimitedDto;
import yehor.budget.web.dto.full.CategoryFullDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

import static yehor.budget.web.exception.CategoryExceptionProvider.cannotDeleteCategoryWithDependentExpensesException;
import static yehor.budget.web.exception.CategoryExceptionProvider.categoryAlreadyExistsException;
import static yehor.budget.web.exception.CategoryExceptionProvider.categoryDoesNotExistException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger LOG = LogManager.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    public List<CategoryFullDto> getAll() {
        Iterable<Category> categories = categoryRepository.findAll();
        return StreamSupport.stream(categories.spliterator(), false)
                .map(categoryConverter::convert)
                .toList();
    }

    public void save(CategoryLimitedDto categoryDto) {
        Category category = categoryConverter.convert(categoryDto);
        validateCategoryDoNotExist(category);
        categoryRepository.save(category);
        LOG.info("{} is saved", category);
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
            LOG.info("Category with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw categoryDoesNotExistException(id);
        } catch (DataIntegrityViolationException e) {
            throw cannotDeleteCategoryWithDependentExpensesException();
        }
    }

    @Transactional
    public void update(CategoryFullDto categoryDto) {
        validateCategoryExists(categoryDto.getId());
        Category category = categoryConverter.convert(categoryDto);
        categoryRepository.update(category);
        LOG.info("{} is updated", category);
    }

    private void validateCategoryDoNotExist(Category category) {
        categoryRepository.findByName(category.getName())
                .ifPresent(e -> {
                    throw categoryAlreadyExistsException(category.getName());
                });
    }

    private void validateCategoryExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw categoryDoesNotExistException(id);
        }
    }
}
