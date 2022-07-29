package common.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MvcClient {

    private static final Logger LOG = LogManager.getLogger(MvcClient.class);

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public MvcClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public void makePost(String url, Object content) {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(content)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOG.error("Error when trying to send Post request to url {} with content {}", url, content, e);
            throw new RuntimeException(e);
        }
    }

    public <T> T makeGet(String url, Parameter parameter, Class<T> responseType) {
        try {
        String response = mockMvc.perform(get(url).param(parameter.key(), parameter.value()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, responseType);
        } catch (Exception e) {
            LOG.error("Error when trying to send Get request to url {} with parameter {} and getting response type {}", url, parameter, responseType, e);
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> makeGetList(String url, List<Parameter> parameters, Class<T> responseType) {
        try {
            MockHttpServletRequestBuilder requestBuilder = fillWithParameters(parameters, get(url));
            ObjectReader listReader = objectMapper.readerForListOf(responseType);

            String response = mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            return listReader.readValue(response);
        } catch (Exception e) {
            LOG.error("Error when trying to send Get request to url {} with parameters {} and getting response type {}", url, parameters, responseType, e);
            throw new RuntimeException(e);
        }
    }

    public void makePut(String url, Object content) {
        try {
            mockMvc.perform(put(url)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(content)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOG.error("Error when trying to send Put request to url {} with content {}", url, content, e);
            throw new RuntimeException(e);
        }
    }

    public static Parameter parameter(String key, String value) {
        return new Parameter(key, value);
    }

    private MockHttpServletRequestBuilder fillWithParameters(List<Parameter> parameters, MockHttpServletRequestBuilder builder) {
        MockHttpServletRequestBuilder filledBuilder = builder;
        for (Parameter parameter : parameters) {
            filledBuilder = filledBuilder.param(parameter.key, parameter.value);
        }
        return filledBuilder;
    }

    private record Parameter(String key, String value) {
    }
}
