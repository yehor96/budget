package yehor.budget.web.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
public class CategoryExceptionProvider {
    public static ResponseStatusException categoryAlreadyExistsException(String name) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Category " + name + " already exists");
    }

    public static ResponseStatusException categoryDoesNotExistException(Long id) {
        return new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Category with id " + id + " does not exist");
    }

    public static ResponseStatusException cannotDeleteCategoryWithDependentExpensesException() {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category with dependent expenses");
    }

    public static ResponseStatusException invalidCategoryIdException(Long categoryId) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Provided category id is not valid - " + categoryId + ". Please provide valid category id");
    }
}
