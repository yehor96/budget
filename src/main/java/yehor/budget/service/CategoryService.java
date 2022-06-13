package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import yehor.budget.entity.Category;
import yehor.budget.exception.CategoryExceptionProvider;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.CategoryDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    public List<CategoryDto> getAll() {
        Iterable<Category> categories = categoryRepository.findAll();
        return StreamSupport.stream(categories.spliterator(), false)
                .map(categoryConverter::convert)
                .toList();
    }

    public void save(CategoryDto categoryDto) {
        Category category = categoryConverter.convert(categoryDto);
        validateCategoryDoNotExist(category);
        categoryRepository.save(category);
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw CategoryExceptionProvider.getCategoryDoesNotExistException(id);
        }
    }

    @Transactional
    public void update(CategoryDto categoryDto) {
        validateCategoryExists(categoryDto.getId());
        Category category = categoryConverter.convert(categoryDto);
        categoryRepository.update(category);
    }

    private void validateCategoryDoNotExist(Category category) {
        categoryRepository.findByName(category.getName())
                .ifPresent(e -> {
                    throw CategoryExceptionProvider.getCategoryAlreadyExistsException(category.getName());
                });
    }

    private void validateCategoryExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw CategoryExceptionProvider.getCategoryDoesNotExistException(id);
        }
    }
}
