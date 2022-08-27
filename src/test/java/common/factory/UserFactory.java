package common.factory;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;
import yehor.budget.entity.User;

@UtilityClass
public class UserFactory {

    public static final String DEFAULT_USERNAME = "username";

    public static UserDetails defaultUser() {
        return User.builder()
                .username(DEFAULT_USERNAME)
                .password("pass")
                .build();
    }
}
