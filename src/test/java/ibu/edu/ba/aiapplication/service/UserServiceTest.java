package ibu.edu.ba.aiapplication.service;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import ibu.edu.ba.aiapplication.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private static final String TEST_EMAIL = "test@ibu.ba";
    private static final String TEST_NAME = "Test User";

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_EMAIL, TEST_NAME);
    }

    @Test
    void shouldGetUserByEmail() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        User result = userService.getUserByEmail(TEST_EMAIL);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // given
        String nonExistentEmail = "nonexistent@ibu.ba";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.getUserByEmail(nonExistentEmail))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void shouldCreateUser() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User result = userService.createUser(testUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // when/then
        assertThatThrownBy(() -> userService.createUser(testUser))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("User with this email already exists");
    }

    @Test
    void shouldUpdateUser() {
        // given
        User updatedUser = new User(TEST_EMAIL, "Updated Name");
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // when
        User result = userService.updateUser(TEST_EMAIL, updatedUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // given
        String nonExistentEmail = "nonexistent@ibu.ba";
        User updatedUser = new User(nonExistentEmail, "Updated Name");
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.updateUser(nonExistentEmail, updatedUser))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    void shouldDeleteUser() {
        // given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // when
        userService.deleteUser(TEST_EMAIL);

        // then
        verify(userRepository).delete(testUser);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // given
        String nonExistentEmail = "nonexistent@ibu.ba";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.deleteUser(nonExistentEmail))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("User not found");
    }
}
