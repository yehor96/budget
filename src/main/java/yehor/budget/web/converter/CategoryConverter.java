package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Category;
import yehor.budget.web.dto.CategoryDto;

@Component
public class CategoryConverter {

    public CategoryDto convert(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category convert(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
