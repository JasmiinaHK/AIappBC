package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserByEmail_Success() {
        // Given
        String email = "test@test.com";
        User user = new User("Test User", email);
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> response = userController.getUserByEmail(email);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User responseUser = (User) response.getBody();
        assertEquals(email, responseUser.getEmail());
        assertEquals("Test User", responseUser.getName());
        verify(userService).getUserByEmail(email);
    }

    @Test
    void getUserByEmail_NotFound() {
        // Given
        String email = "nonexistent@test.com";
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.getUserByEmail(email);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getUserByEmail(email);
    }

    @Test
    void getUserNameByEmail_Success() {
        // Given
        String email = "test@test.com";
        User user = new User("Test User", email);
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> response = userController.getUserNameByEmail(email);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        assertEquals("Test User", response.getBody());
        verify(userService).getUserByEmail(email);
    }

    @Test
    void getUserNameByEmail_NotFound() {
        // Given
        String email = "nonexistent@test.com";
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.getUserNameByEmail(email);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getUserByEmail(email);
    }
}
