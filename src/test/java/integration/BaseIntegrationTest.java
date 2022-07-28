package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.client.MvcClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import yehor.budget.BudgetApplication;

@AutoConfigureMockMvc
@SpringBootTest(classes = BudgetApplication.class)
public class BaseIntegrationTest {

    protected static final String BASE_URL = "http://localhost:8080/";

    protected static final String EXPENSES_URL = BASE_URL.concat("/api/v1/expenses/");
    protected static final String CATEGORIES_URL = BASE_URL.concat("/api/v1/categories/");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MvcClient mvcClient;

    @BeforeEach
    void setUp() {
        mvcClient = new MvcClient(mockMvc, objectMapper);
    }

}
