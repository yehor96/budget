package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.CategoryService;
import yehor.budget.web.dto.full.CategoryFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public List<CategoryFullDto> getAllCategories() {
        return categoryService.getAll();
    }

    @PostMapping
    @Operation(summary = "Save category")
    public ResponseEntity<CategoryFullDto> saveCategory(@RequestBody CategoryLimitedDto categoryDto) {
        try {
            CategoryFullDto saved = categoryService.save(categoryDto);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (ObjectAlreadyExistsException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete category by id")
    public ResponseEntity<CategoryLimitedDto> deleteCategory(@RequestParam("id") Long id) {
        try {
            categoryService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update category by id")
    public ResponseEntity<CategoryFullDto> updateCategory(@RequestBody CategoryFullDto categoryDto) {
        try {
            CategoryFullDto updated = categoryService.update(categoryDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
    }
}
