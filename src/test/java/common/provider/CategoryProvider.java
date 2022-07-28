package common.provider;

import lombok.experimental.UtilityClass;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

@UtilityClass
public class CategoryProvider {

    private static final long DEFAULT_ID = 1L;

    public static CategoryLimitedDto defaultCategoryLimitedDto() {
        return CategoryLimitedDto.builder()
                .name("Food")
                .build();
    }
}
