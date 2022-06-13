package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
public class ExpenseExceptionProvider {

    public static ResponseStatusException getExpenseWithIdDoesNotExistException(Long id) {
        return new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Expense with id " + id + " not found");
    }

    public static ResponseStatusException getExpenseWithIdExistsException(Long id) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Expense with id " + id + " already exists");
    }
}
