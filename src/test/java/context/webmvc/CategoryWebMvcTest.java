package context.webmvc;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.CategoryService;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;

import static common.factory.CategoryFactory.DEFAULT_CATEGORY_ID;
import static common.factory.CategoryFactory.defaultCategoryFullDto;
import static common.factory.CategoryFactory.defaultCategoryFullDtoList;
import static common.factory.CategoryFactory.defaultCategoryLimitedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private CategoryService categoryService;

    // Get all categories

    @Test
    void testGetAllCategories() throws Exception {
        List<CategoryFullDto> expectedCategories = defaultCategoryFullDtoList();

        when(categoryService.getAll()).thenReturn(expectedCategories);

        String response = mockMvc.perform(get(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectReader listReader = objectMapper.readerForListOf(CategoryFullDto.class);
        List<CategoryFullDto> actualCategories = listReader.readValue(response);

        assertEquals(expectedCategories, actualCategories);
    }

    // Save category

    @Test
    void testSaveCategory() throws Exception {
        CategoryLimitedDto category = defaultCategoryLimitedDto();

        mockMvc.perform(post(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).save(category);
    }

    @Test
    void testTrySavingCategoryWhenSuchAlreadyExists() throws Exception {
        CategoryLimitedDto category = defaultCategoryLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectAlreadyExistsException(expectedErrorMessage))
                .when(categoryService).save(category);

        String response = mockMvc.perform(post(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Delete category

    @Test
    void testDeleteCategory() throws Exception {
        mockMvc.perform(delete(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_CATEGORY_ID)))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).delete(DEFAULT_CATEGORY_ID);
    }

    @Test
    void testTryDeletingCategoryWhenSuchDoesNotExists() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(categoryService).delete(DEFAULT_CATEGORY_ID);

        String response = mockMvc.perform(delete(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_CATEGORY_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    @Test
    void testTryDeletingCategoryWithDependentExpenses() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(categoryService).delete(DEFAULT_CATEGORY_ID);

        String response = mockMvc.perform(delete(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_CATEGORY_ID)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Update category

    @Test
    void testUpdateCategory() throws Exception {
        CategoryFullDto category = defaultCategoryFullDto();

        mockMvc.perform(put(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).update(category);
    }

    @Test
    void testTryUpdatingCategoryWhenSuchDoesNotExists() throws Exception {
        CategoryFullDto category = defaultCategoryFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(categoryService).update(category);

        String response = mockMvc.perform(put(CATEGORIES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
