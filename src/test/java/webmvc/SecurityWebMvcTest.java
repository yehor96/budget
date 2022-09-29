package webmvc;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityWebMvcTest extends BaseWebMvcTest {

    @ParameterizedTest
    @MethodSource("endpointProvider")
    void testEndpointsNotAccessibleWhenUnauthorized(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
    }

    static Stream<String> endpointProvider() {
        return Stream.of(
                SETTINGS_URL,
                INCOME_SOURCES_URL,
                CATEGORIES_URL,
                STATISTICS_URL,
                EXPENSES_URL,
                REGULAR_EXPECTED_EXPENSES_URL,
                INCOME_SOURCES_URL
        );
    }
}
