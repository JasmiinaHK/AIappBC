package ibu.edu.ba.aiapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibu.edu.ba.aiapplication.configuration.WebTestConfig;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import ibu.edu.ba.aiapplication.rest.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(WebTestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
    }

    @Test
    void getAllUsers_ReturnsUsers() throws Exception {
        when(userRepository.findAll())
            .thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].email").value("test@example.com"))
            .andExpect(jsonPath("$[0].name").value("Test User"));

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_ReturnsEmptyList() throws Exception {
        when(userRepository.findAll())
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void getUserByEmail_ReturnsUser() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/test@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_ReturnsNotFound() throws Exception {
        when(userRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/nonexistent@example.com"))
            .andExpect(status().isNotFound());

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void createUser_ReturnsCreatedUser() throws Exception {
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        User invalidUser = new User();
        invalidUser.setEmail("");
        invalidUser.setName("");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
            .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ReturnsUpdatedUser() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        mockMvc.perform(put("/api/users/test@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithNonexistentEmail_ReturnsNotFound() throws Exception {
        when(userRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/nonexistent@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isNotFound());

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ReturnsOk() throws Exception {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));

        mockMvc.perform(delete("/api/users/test@example.com"))
            .andExpect(status().isOk());

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).delete(any(User.class));
    }

    @Test
    void deleteUser_WithNonexistentEmail_ReturnsNotFound() throws Exception {
        when(userRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/nonexistent@example.com"))
            .andExpect(status().isNotFound());

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).delete(any(User.class));
    }
}
