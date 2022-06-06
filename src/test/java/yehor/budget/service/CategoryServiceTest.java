package yehor.budget.service;

import org.junit.jupiter.api.Test;
import yehor.budget.entity.Category;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.CategoryDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private final CategoryRepository categoryRepositoryMock = mock(CategoryRepository.class);
    private final CategoryConverter categoryConverterMock = mock(CategoryConverter.class);

    private final CategoryService categoryService = new CategoryService(categoryRepositoryMock, categoryConverterMock);

    @Test
    void testGetAll() {
        CategoryDto expectedCategoryDto1 = CategoryDto.builder().name("Food").build();
        CategoryDto expectedCategoryDto2 = CategoryDto.builder().name("Meds").build();
        Category expectedCategory1 = Category.builder().name("Food").build();
        Category expectedCategory2 = Category.builder().name("Meds").build();

        when(categoryRepositoryMock.findAll()).thenReturn(List.of(expectedCategory1, expectedCategory2));
        when(categoryConverterMock.convertToDto(expectedCategory1)).thenReturn(expectedCategoryDto1);
        when(categoryConverterMock.convertToDto(expectedCategory2)).thenReturn(expectedCategoryDto2);

        List<CategoryDto> categories = categoryService.getAll();

        assertTrue(categories.contains(expectedCategoryDto1));
        assertTrue(categories.contains(expectedCategoryDto2));
    }

    @Test
    void testSave() {
        CategoryDto expectedCategoryDto = CategoryDto.builder().name("Food").build();
        Category expectedCategory = Category.builder().name("Food").build();

        when(categoryConverterMock.convertToEntity(expectedCategoryDto)).thenReturn(expectedCategory);

        categoryService.save(expectedCategoryDto);

        verify(categoryRepositoryMock, times(1))
                .save(expectedCategory);
    }

}
