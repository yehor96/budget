package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Category;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.web.converter.CategoryConverter;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;
import java.util.Optional;

import static common.factory.CategoryFactory.defaultCategory;
import static common.factory.CategoryFactory.defaultCategoryFullDto;
import static common.factory.CategoryFactory.defaultCategoryLimitedDto;
import static common.factory.CategoryFactory.secondCategory;
import static common.factory.CategoryFactory.secondCategoryFullDto;
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
        CategoryFullDto expectedCategoryDto1 = defaultCategoryFullDto();
        CategoryFullDto expectedCategoryDto2 = secondCategoryFullDto();
        Category expectedCategory1 = defaultCategory();
        Category expectedCategory2 = secondCategory();

        when(categoryRepositoryMock.findAll()).thenReturn(List.of(expectedCategory1, expectedCategory2));
        when(categoryConverterMock.convert(expectedCategory1)).thenReturn(expectedCategoryDto1);
        when(categoryConverterMock.convert(expectedCategory2)).thenReturn(expectedCategoryDto2);

        List<CategoryFullDto> categories = categoryService.getAll();

        assertTrue(categories.contains(expectedCategoryDto1));
        assertTrue(categories.contains(expectedCategoryDto2));
    }

    @Test
    void testSave() {
        CategoryLimitedDto expectedCategoryDto = defaultCategoryLimitedDto();
        Category expectedCategory = defaultCategory();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);

        categoryService.save(expectedCategoryDto);

        verify(categoryRepositoryMock, times(1))
                .save(expectedCategory);
    }

    @Test
    void testTrySavingExistingCategory() {
        CategoryLimitedDto expectedCategoryDto = defaultCategoryLimitedDto();
        Category expectedCategory = defaultCategory();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.findByName(expectedCategory.getName())).thenReturn(Optional.of(expectedCategory));

        try {
            categoryService.save(expectedCategoryDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectAlreadyExistsException.class, e.getClass());
            ObjectAlreadyExistsException exception = (ObjectAlreadyExistsException) e;
            assertEquals("Category " + expectedCategory.getName() + " already exists", exception.getMessage());
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
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + 1L + " not found", exception.getMessage());
            verify(categoryRepositoryMock, times(1))
                    .deleteById(1L);
        }
    }

    @Test
    void testUpdateCategory() {
        CategoryFullDto expectedCategoryDto = defaultCategoryFullDto();
        Category expectedCategory = defaultCategory();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.existsById(expectedCategoryDto.getId())).thenReturn(true);

        categoryService.update(expectedCategoryDto);

        verify(categoryRepositoryMock, times(1))
                .save(expectedCategory);
    }

    @Test
    void testTryUpdatingNotExistingCategory() {
        CategoryFullDto expectedCategoryDto = defaultCategoryFullDto();
        Category expectedCategory = defaultCategory();

        when(categoryConverterMock.convert(expectedCategoryDto)).thenReturn(expectedCategory);
        when(categoryRepositoryMock.existsById(expectedCategoryDto.getId())).thenReturn(false);

        try {
            categoryService.update(expectedCategoryDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + 1L + " does not exist", exception.getMessage());
            verify(categoryRepositoryMock, never())
                    .save(expectedCategory);
        }
    }

    @Test
    void testTryDeletingCategoryWithDependentExpenses() {
        Long id = 1L;

        doThrow(new DataIntegrityViolationException("")).when(categoryRepositoryMock).deleteById(1L);

        try {
            categoryService.delete(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            IllegalArgumentException exception = (IllegalArgumentException) e;
            assertEquals("Cannot delete category with dependent expenses", exception.getMessage());
            verify(categoryRepositoryMock, times(1))
                    .deleteById(id);
        }
    }

}
