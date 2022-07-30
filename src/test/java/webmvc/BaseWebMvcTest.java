package webmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import yehor.budget.BudgetApplication;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@AutoConfigureMockMvc
@SpringBootTest(classes = BudgetApplication.class)
abstract class BaseWebMvcTest {

    protected static final String BASE_URL = "http://localhost:8080/";

    protected static final String EXPENSES_URL = BASE_URL.concat("/api/v1/expenses/");
    protected static final String EXPENSE_INTERVAL_URL = EXPENSES_URL + "/interval";
    protected static final String EXPENSE_SUM_URL = EXPENSES_URL + "/sum";

    protected static final String CATEGORIES_URL = BASE_URL.concat("/api/v1/categories/");
    protected static final String SETTINGS_URL = "/api/v1/settings/";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    protected void verifyResponseErrorObject(String responseContent, HttpStatus status, String message) {
        try {

            var response = objectMapper.readValue(responseContent, HashMap.class);

            assertEquals(status.value(), response.get("status"));
            assertEquals(status.getReasonPhrase(), response.get("error"));
            assertEquals(message, response.get("message"));
            assertNotNull(response.get("path"));
            assertNotNull(response.get("timestamp"));

        } catch (JsonProcessingException e) {
            fail("Response Error Object is not correct", e);
        }
    }
}
