package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.CategoryService;
import yehor.budget.web.dto.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(description = "Get all categories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAll();
    }

    @PostMapping
    @Operation(description = "Add category")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete category by id")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(description = "Update category by id")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("id") Long id,
                                                      @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(id);
        categoryService.update(categoryDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
