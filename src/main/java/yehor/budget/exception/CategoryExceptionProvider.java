package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
public class CategoryExceptionProvider {
    public static ResponseStatusException getCategoryAlreadyExists(String name) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Category " + name + " already exists");
    }
}
