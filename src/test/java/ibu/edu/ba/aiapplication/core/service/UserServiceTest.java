package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByEmail_Success() {
        // Given
        String email = "test@test.com";
        User user = new User("Test User", email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Test User", result.get().getName());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_NotFound() {
        // Given
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserNameByEmail_Success() {
        // Given
        String email = "test@test.com";
        User user = new User("Test User", email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        String name = userService.getUserNameByEmail(email);

        // Then
        assertEquals("Test User", name);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserNameByEmail_NotFound() {
        // Given
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.getUserNameByEmail(email));
        verify(userRepository).findByEmail(email);
    }
}
