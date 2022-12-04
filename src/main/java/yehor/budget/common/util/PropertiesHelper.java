package yehor.budget.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PropertiesHelper {

    private final Environment environment;

    public Boolean getBooleanProperty(String key) {
        return Boolean.TRUE.equals(environment.getProperty(key, Boolean.class));
    }

    public Integer getIntProperty(String key) {
        Integer property = environment.getProperty(key, Integer.class);
        return Objects.nonNull(property) ? property : 0;
    }

    public String getStringProperty(String key) {
        String property = environment.getProperty(key, String.class);
        return Objects.nonNull(property) ? property : "";
    }

}
