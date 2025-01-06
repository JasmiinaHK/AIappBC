package ibu.edu.ba.aiapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibu.edu.ba.aiapplication.configuration.WebTestConfig;
import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import ibu.edu.ba.aiapplication.rest.controller.TaskController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)  // WebMvcTest keeps controller layer isolated
@Import(WebTestConfig.class)       // Import your custom configuration
@SpringJUnitConfig                 // Ensures Spring testing configuration is correctly applied
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setSubject("Math");
        testTask.setGrade("5th");
        testTask.setLessonUnit("Algebra");
        testTask.setMaterialType("Worksheet");
        testTask.setUserEmail("test@example.com");
        testTask.setContent("Test content");
        testTask.setLanguage("en");
    }

    @Test
    void getAllTasks_ReturnsOk() throws Exception {
        when(taskService.getAllTasks())
                .thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].subject").value("Math"))
                .andExpect(jsonPath("$[0].grade").value("5th"))
                .andExpect(jsonPath("$[0].lessonUnit").value("Algebra"));

        verify(taskService).getAllTasks();
    }

    @Test
    void getAllTasks_ReturnsEmptyList() throws Exception {
        when(taskService.getAllTasks())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(taskService).getAllTasks();
    }

    @Test
    void getTasksByUser_ReturnsOk() throws Exception {
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        when(taskService.getTasksByUserEmail("test@example.com"))
                .thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/api/tasks/user/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].subject").value("Math"))
                .andExpect(jsonPath("$[0].userEmail").value("test@example.com"));

        verify(userRepository).existsByEmail("test@example.com");
        verify(taskService).getTasksByUserEmail("test@example.com");
    }

    @Test
    void getTasksByUser_WithNonexistentUser_ReturnsForbidden() throws Exception {
        when(userRepository.existsByEmail("nonexistent@example.com"))
                .thenReturn(false);

        mockMvc.perform(get("/api/tasks/user/nonexistent@example.com"))
                .andExpect(status().isForbidden());

        verify(userRepository).existsByEmail("nonexistent@example.com");
        verify(taskService, never()).getTasksByUserEmail(anyString());
    }

    @Test
    void getTaskById_ReturnsOk() throws Exception {
        when(taskService.getTaskById(1L))
                .thenReturn(testTask);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subject").value("Math"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void getTaskById_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(99L))
                .thenReturn(null);

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(99L);
    }

    @Test
    void createTask_ReturnsOk() throws Exception {
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        when(taskService.createTask(any(Task.class)))
                .thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subject").value("Math"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));

        verify(userRepository).existsByEmail("test@example.com");
        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void createTask_WithNonexistentUser_ReturnsForbidden() throws Exception {
        testTask.setUserEmail("nonexistent@example.com");
        when(userRepository.existsByEmail("nonexistent@example.com"))
                .thenReturn(false);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isForbidden());

        verify(userRepository).existsByEmail("nonexistent@example.com");
        verify(taskService, never()).createTask(any(Task.class));
    }

    @Test
    void updateTask_ReturnsOk() throws Exception {
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        when(taskService.updateTask(eq(1L), any(Task.class)))
                .thenReturn(testTask);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subject").value("Math"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));

        verify(userRepository).existsByEmail("test@example.com");
        verify(taskService).updateTask(eq(1L), any(Task.class));
    }

    @Test
    void updateTask_WithNonexistentTask_ReturnsNotFound() throws Exception {
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);
        when(taskService.updateTask(eq(99L), any(Task.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isNotFound());

        verify(userRepository).existsByEmail("test@example.com");
        verify(taskService).updateTask(eq(99L), any(Task.class));
    }

    @Test
    void deleteTask_ReturnsOk() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1L);
    }
}
