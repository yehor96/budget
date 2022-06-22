package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@UtilityClass
public class ExpenseExceptionProvider {

    public static ResponseStatusException expenseWithIdDoesNotExistException(Long id) {
        return new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Expense with id " + id + " not found");
    }
}
