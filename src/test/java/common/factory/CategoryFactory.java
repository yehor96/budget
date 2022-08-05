package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.Category;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;

@UtilityClass
public class CategoryFactory {

    public static final long DEFAULT_CATEGORY_ID = 1L;

    public static CategoryLimitedDto defaultCategoryLimitedDto() {
        return CategoryLimitedDto.builder()
                .name("Food")
                .build();
    }

    public static CategoryFullDto defaultCategoryFullDto() {
        return CategoryFullDto.builder()
                .id(DEFAULT_CATEGORY_ID)
                .name("Food")
                .build();
    }

    public static Category defaultCategory() {
        return Category.builder()
                .id(DEFAULT_CATEGORY_ID)
                .name("Food")
                .build();
    }

    public static CategoryFullDto secondCategoryFullDto() {
        return CategoryFullDto.builder()
                .id(2L)
                .name("Meds")
                .build();
    }

    public static Category secondCategory() {
        return Category.builder()
                .id(2L)
                .name("Meds")
                .build();
    }

    public static CategoryFullDto thirdCategoryFullDto() {
        return CategoryFullDto.builder()
                .id(3L)
                .name("Transportation")
                .build();
    }

    public static List<CategoryFullDto> defaultCategoryFullDtoList() {
        CategoryFullDto categoryFullDto1 = defaultCategoryFullDto();
        CategoryFullDto categoryFullDto2 = secondCategoryFullDto();
        CategoryFullDto categoryFullDto3 = thirdCategoryFullDto();
        return List.of(categoryFullDto1, categoryFullDto2, categoryFullDto3);
    }

}
