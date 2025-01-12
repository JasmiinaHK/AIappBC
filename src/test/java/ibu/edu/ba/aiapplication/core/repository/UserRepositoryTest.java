package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        // Given
        User user = new User("Test User", "test@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getName());
        verify(userRepository).save(user);
    }

    @Test
    void testFindByEmail_Exists() {
        // Given
        String email = "test@example.com";
        User user = new User("Test User", email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> found = userRepository.findByEmail(email);

        // Then
        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
        assertEquals("Test User", found.get().getName());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_NotExists() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> found = userRepository.findByEmail(email);

        // Then
        assertFalse(found.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testDeleteUser() {
        // Given
        Long id = 1L;
        User user = new User("Test User", "test@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        userRepository.deleteById(id);

        // Then
        verify(userRepository).deleteById(id);
    }
}
