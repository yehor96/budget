package context;

import common.response.exchangerate.ExchangeRateResponseProvider;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yehor.budget.BudgetApplication;
import yehor.budget.common.exception.InternalClientException;
import yehor.budget.common.Currency;
import yehor.budget.service.client.currency.ExchangeRateClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.INTERNAL_SERVER_ERROR_500;
import static org.mockserver.model.HttpStatusCode.OK_200;
import static org.mockserver.model.MediaType.APPLICATION_JSON;
import static yehor.budget.common.Currency.UAH;
import static yehor.budget.common.Currency.USD;

@SpringBootTest(classes = BudgetApplication.class)
@MockServerTest({"api.exchangerate.host.url=http://localhost:${mockServerPort}"})
class ExchangeRateClientTest {

    private MockServerClient mockServerClient;

    @Autowired
    private ExchangeRateClient exchangeRateClient;

    @Test
    void testSuccessClientResponseInProcessed() {
        Currency from = UAH;
        Currency to = USD;
        BigDecimal value = BigDecimal.valueOf(35.5);
        String body = ExchangeRateResponseProvider.responseBody(from.toString(), to.toString(), value);

        mockServerClient
                .when(request()
                        .withQueryStringParameter("from", from.toString())
                        .withQueryStringParameter("to", to.toString()))
                .respond(response()
                        .withStatusCode(OK_200.code())
                        .withContentType(APPLICATION_JSON)
                        .withBody(body));

        BigDecimal rate = exchangeRateClient.rate(from, to);
        assertEquals(rate, value);
    }

    @Test
    void testFailedClientResponseThrowsInternalClientException() {
        Currency from = UAH;
        Currency to = USD;

        mockServerClient
                .when(request()
                        .withQueryStringParameter("from", from.toString())
                        .withQueryStringParameter("to", to.toString()))
                .respond(response()
                        .withStatusCode(INTERNAL_SERVER_ERROR_500.code()));
        try {
            exchangeRateClient.rate(from, to);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(InternalClientException.class, e.getClass());
            InternalClientException exception = (InternalClientException) e;
            assertTrue(exception.getMessage().startsWith("Not able to perform a request to"));
        }
    }

    @Test
    void testClientResponseContainsUnexpectedJsonAndThrowsInternalClientException() {
        Currency from = UAH;
        Currency to = USD;
        String invalidBody = "{\"someObj\":\"someOtherObj\"}";

        mockServerClient
                .when(request()
                        .withQueryStringParameter("from", from.toString())
                        .withQueryStringParameter("to", to.toString()))
                .respond(response()
                        .withStatusCode(OK_200.code())
                        .withContentType(APPLICATION_JSON)
                        .withBody(invalidBody));
        try {
            exchangeRateClient.rate(from, to);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(InternalClientException.class, e.getClass());
            InternalClientException exception = (InternalClientException) e;
            assertTrue(exception.getMessage().startsWith("Not able to perform a request to"));
        }
    }
}