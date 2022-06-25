package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Category;
import yehor.budget.web.dto.limited.CategoryLimitedDto;
import yehor.budget.web.dto.full.CategoryFullDto;

@Component
public class CategoryConverter {

    public CategoryFullDto convert(Category category) {
        return CategoryFullDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category convert(CategoryLimitedDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public Category convert(CategoryFullDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
