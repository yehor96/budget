package context.webmvc;

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

    protected static final String HOST = "localhost";
    protected static final String PORT = "8080";

    protected static final String BASE_URL = "http://".concat(HOST).concat(PORT).concat("/api/v1");

    protected static final String EXPENSES_URL = BASE_URL.concat("/expenses");
    protected static final String CATEGORIES_URL = BASE_URL.concat("/categories");
    protected static final String SETTINGS_URL = BASE_URL.concat("/settings");
    protected static final String STATISTICS_URL = BASE_URL.concat("/statistics");
    protected static final String TAGS_URL = BASE_URL.concat("/tags");
    protected static final String ESTIMATED_EXPENSES_URL = BASE_URL.concat("/estimated-expenses");
    protected static final String INCOME_SOURCES_URL = BASE_URL.concat("/income-sources");
    protected static final String ACTORS_URL = BASE_URL.concat("/actors");
    protected static final String BALANCE_URL = BASE_URL.concat("/balance");
    protected static final String STORAGE_URL = BASE_URL.concat("/storage");

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
