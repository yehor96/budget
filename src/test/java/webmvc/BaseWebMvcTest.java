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

    protected static final String BASE_URL = "http://localhost:8080/api/v1";

    protected static final String EXPENSES_URL = BASE_URL.concat("/expenses");
    protected static final String EXPENSE_INTERVAL_URL = EXPENSES_URL + "/interval";
    protected static final String EXPENSE_SUM_URL = EXPENSES_URL + "/sum";

    protected static final String CATEGORIES_URL = BASE_URL.concat("/categories");

    protected static final String SETTINGS_URL = BASE_URL.concat("/settings");

    protected static final String STATISTICS_URL = BASE_URL.concat("/statistics");
    protected static final String MONTHLY_STATISTICS_URL = STATISTICS_URL.concat("/monthly");
    protected static final String PERIODIC_STATISTICS_URL = STATISTICS_URL.concat("/periodic");

    protected static final String TAGS_URL = BASE_URL.concat("/tags");


    protected static final String BASIC_AUTH_STRING = "Basic YWRtaW46cGFzc3dvcmQ=";

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
