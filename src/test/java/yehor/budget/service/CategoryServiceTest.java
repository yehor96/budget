package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import yehor.budget.entity.Category;
import yehor.budget.exception.CustomResponseStatusException;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private final CategoryRepository categoryRepositoryMock = mock(CategoryRepository.class);
    private final CategoryConverter categoryConverterMock = mock(CategoryConverter.class);

    private final CategoryService categoryService = new CategoryService(categoryRepositoryMock, categoryConverterMock);

    @Test
    void testGetAll() {
        CategoryFullDto expectedCategoryDto1 = CategoryFullDto.builder().id(1L).name("Food").build();
        CategoryFullDto expectedCategoryDto2 = CategoryFullDto.builder().id(2L).name("Meds").build();
        Category expectedCategory1 = Category.builder().id(1L).name("Food").build();
        Category expectedCategory2 = Category.builder().id(2L).name("Meds").build();

        when(categoryRepositoryMock.findAll()).thenReturn(List.of(expectedCategory1, expectedCategory2));
        when(categoryConverterMock.convert(expectedCategory1)).thenReturn(expectedCategoryDto1);
        when(categoryConverterMock.convert(expectedCategory2)).thenReturn(expectedCategoryDto2);

        List<CategoryFullDto> categories = categoryService.getAll();

        assertTrue(categories.contains(expectedCategoryDto1));
        assertTrue(categories.contains(expectedCategoryDto2));
    }

    @Test
    void testSave() {
        CategoryLimitedDto expectedCategoryDto = CategoryLimitedDto.builder().name("Food").build();
        Category expectedCategory = Category.builder().name("Food").build();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);

        categoryService.save(expectedCategoryDto);

        verify(categoryRepositoryMock, times(1))
                .save(expectedCategory);
    }

    @Test
    void testTrySavingExistingCategory() {
        CategoryLimitedDto expectedCategoryDto = CategoryLimitedDto.builder().name("Food").build();
        Category expectedCategory = Category.builder().name("Food").build();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.findByName(expectedCategory.getName())).thenReturn(Optional.of(expectedCategory));

        try {
            categoryService.save(expectedCategoryDto);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals("Category " + expectedCategory.getName() + " already exists", e.getReason());
            verify(categoryRepositoryMock, never())
                    .save(expectedCategory);
        }
    }

    @Test
    void testDeleteCategory() {
        categoryService.delete(1L);
        verify(categoryRepositoryMock, times(1))
                .deleteById(1L);
    }

    @Test
    void testTryDeletingNotExistingCategory() {
        doThrow(new EmptyResultDataAccessException(1)).when(categoryRepositoryMock).deleteById(1L);

        try {
            categoryService.delete(1L);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Category with id " + 1L + " does not exist", e.getReason());
            verify(categoryRepositoryMock, times(1))
                    .deleteById(1L);
        }
    }

    @Test
    void testUpdateCategory() {
        CategoryFullDto expectedCategoryDto = CategoryFullDto.builder().id(1L).name("Food").build();
        Category expectedCategory = Category.builder().id(1L).name("Food").build();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.existsById(expectedCategoryDto.getId())).thenReturn(true);

        categoryService.update(expectedCategoryDto);

        verify(categoryRepositoryMock, times(1))
                .update(expectedCategory);
    }

    @Test
    void testTryUpdatingNotExistingCategory() {
        CategoryFullDto expectedCategoryDto = CategoryFullDto.builder().id(1L).name("Food").build();
        Category expectedCategory = Category.builder().id(1L).name("Food").build();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.existsById(expectedCategoryDto.getId())).thenReturn(false);

        try {
            categoryService.update(expectedCategoryDto);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Category with id " + expectedCategoryDto.getId() + " does not exist", e.getReason());
            verify(categoryRepositoryMock, never())
                    .update(expectedCategory);
        }
    }

    @Test
    void testTryDeletingCategoryWithDependentExpenses() {
        Long id = 1L;

        doThrow(new DataIntegrityViolationException("")).when(categoryRepositoryMock).deleteById(1L);

        try {
            categoryService.delete(id);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals("Cannot delete category with dependent expenses", e.getReason());
            verify(categoryRepositoryMock, times(1))
                    .deleteById(id);
        }
    }

}
