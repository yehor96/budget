package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@UtilityClass
public class ExpenseExceptionProvider {

    public static ResponseStatusException getNoExpenseForDateException(LocalDate date) {
        return new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Records for " + date + " are not found.");
    }

        public static ResponseStatusException getExpenseInDateAlreadyExistsException(LocalDate date) {
            return new CustomResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Daily expense with provided date " + date + " already exists.");
    }
}
