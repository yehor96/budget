package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import yehor.budget.repository.UserRepository;

import static common.factory.UserFactory.DEFAULT_USERNAME;
import static common.factory.UserFactory.defaultUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepositoryMock = mock(UserRepository.class);

    private final UserService userService = new UserService(userRepositoryMock);

    @Test
    void testUserFound() {
        String username = DEFAULT_USERNAME;
        UserDetails expectedUser = defaultUser();

        when(userRepositoryMock.findByUsername(username)).thenReturn(expectedUser);

        UserDetails actualUser = userService.loadUserByUsername(username);

        verify(userRepositoryMock, times(1)).findByUsername(username);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testUserNotFoundThrowsUsernameNotFoundException() {
        String username = DEFAULT_USERNAME;

        when(userRepositoryMock.findByUsername(username)).thenReturn(null);

        try {
            userService.loadUserByUsername(username);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(UsernameNotFoundException.class, e.getClass());
            UsernameNotFoundException exception = (UsernameNotFoundException) e;
            assertEquals("User with username [" + username + "] was not found", exception.getMessage());
        }
    }
}
