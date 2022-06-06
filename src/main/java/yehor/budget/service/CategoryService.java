package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.entity.Category;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.CategoryDto;

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
                .map(categoryConverter::convertToDto)
                .toList();
    }

    public void save(CategoryDto categoryDto) {
        Category category = categoryConverter.convertToEntity(categoryDto);
        categoryRepository.save(category);
    }
}
