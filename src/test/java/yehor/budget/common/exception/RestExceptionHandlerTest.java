package yehor.budget.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {

    private final RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
    private final MockHttpServletRequest requestMock = mock(MockHttpServletRequest.class);

    @Test
    @SuppressWarnings("unchecked")
    void testExceptionHandlerHandlesUnknownException() {
        RuntimeException exception = new RuntimeException();

        Map<String, Object> expectedBody = new LinkedHashMap<>();
        expectedBody.put("timestamp", LocalDateTime.now());
        expectedBody.put("status", 500);
        expectedBody.put("error", "Internal Server Error");
        expectedBody.put("message", "Unknown error occurred");
        expectedBody.put("path", "/some/api/path");

        when(requestMock.getRequestURI()).thenReturn("/some/api/path");

        ResponseEntity<Object> responseEntity = restExceptionHandler.exceptionHandler(exception, requestMock);

        Map<String, Object> actualBody = (LinkedHashMap<String, Object>) responseEntity.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(actualBody);
        LocalDateTime expectedTimeStamp = ((LocalDateTime) expectedBody.get("timestamp")).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime actualTimeStamp = ((LocalDateTime) actualBody.get("timestamp")).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expectedTimeStamp, actualTimeStamp);
        assertEquals(expectedBody.get("status"), actualBody.get("status"));
        assertEquals(expectedBody.get("error"), actualBody.get("error"));
        assertEquals(expectedBody.get("message"), actualBody.get("message"));
        assertEquals(expectedBody.get("path"), actualBody.get("path"));
    }

    @Test
    void testExceptionHandlerHandlesRethrowsResponseStatusException() {
        ResponseStatusException expectedException =
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Expected exception");

        try {
            restExceptionHandler.exceptionHandler(expectedException, requestMock);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ResponseStatusException.class, e.getClass());
            ResponseStatusException actualException = (ResponseStatusException) e;
            assertEquals(expectedException.getStatus(), actualException.getStatus());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

}