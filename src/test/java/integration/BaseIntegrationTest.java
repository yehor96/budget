package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.client.MvcClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.web.servlet.MockMvc;
import yehor.budget.BudgetApplication;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static common.client.MvcClient.parameter;

@AutoConfigureMockMvc
@SpringBootTest(classes = BudgetApplication.class)
abstract class BaseIntegrationTest {

    protected static final String BASE_URL = "http://localhost:8080/";

    protected static final String EXPENSES_URL = BASE_URL.concat("/api/v1/expenses/");
    protected static final String CATEGORIES_URL = BASE_URL.concat("/api/v1/categories/");
    protected static final String EXPENSE_INTERVAL_URL = EXPENSES_URL + "/interval";

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected DataSource dataSource;

    protected MvcClient mvcClient;

    private static boolean isDatabasePopulated;

    @BeforeEach
    void init() {
        mvcClient = new MvcClient(mockMvc, objectMapper);
        if (!isDatabasePopulated) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScripts(new ClassPathResource("/prepare-db.sql"));
            populator.execute(this.dataSource);
            isDatabasePopulated = true;
        }
    }

    protected void saveExpense(ExpenseLimitedDto expenseLimDto) {
        mvcClient.makePost(EXPENSES_URL, expenseLimDto);
    }

    protected void updateExpense(ExpenseFullDto expenseFullDto) {
        mvcClient.makePut(EXPENSES_URL, expenseFullDto);
    }

    protected ExpenseFullDto getExpenseById(String id) {
        return mvcClient.makeGet(EXPENSES_URL, parameter("id", id), ExpenseFullDto.class);
    }

    protected void saveCategory(CategoryLimitedDto categoryLimitedDto) {
        mvcClient.makePost(CATEGORIES_URL, categoryLimitedDto);
    }

    protected List<ExpenseFullDto> getExpensesInInterval(LocalDate dateFrom, LocalDate dateTo) {

        var intervalDateParameters = List.of(
                parameter("dateFrom", dateFrom.format(DATE_FORMAT)),
                parameter("dateTo", dateTo.format(DATE_FORMAT))
        );

        return mvcClient.makeGetList(EXPENSE_INTERVAL_URL, intervalDateParameters, ExpenseFullDto.class);
    }
}
