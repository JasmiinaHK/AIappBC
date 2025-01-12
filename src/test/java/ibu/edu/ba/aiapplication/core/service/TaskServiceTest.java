package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_Success() {
        // Given
        String userEmail = "test@test.com";
        String subject = "Math";
        String grade = "5";
        String lessonUnit = "Algebra";
        String materialType = "Quiz";
        String generatedContent = "Generated content";

        User user = new User();
        user.setEmail(userEmail);

        Task task = new Task();
        task.setUserEmail(userEmail);
        task.setSubject(subject);
        task.setGrade(grade);
        task.setLessonUnit(lessonUnit);
        task.setMaterialType(materialType);
        task.setGeneratedContent(generatedContent);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        Task createdTask = taskService.createTask(task);

        // Then
        assertNotNull(createdTask);
        assertEquals(subject, createdTask.getSubject());
        assertEquals(grade, createdTask.getGrade());
        assertEquals(lessonUnit, createdTask.getLessonUnit());
        assertEquals(materialType, createdTask.getMaterialType());
        assertEquals(generatedContent, createdTask.getGeneratedContent());
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTasksByUser_Success() {
        // Given
        String userEmail = "test@test.com";
        User user = new User();
        user.setEmail(userEmail);

        Task task = new Task();
        task.setUserEmail(userEmail);
        task.setSubject("Math");
        List<Task> tasks = Arrays.asList(task);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(tasks);

        // When
        List<Task> foundTasks = taskService.getTasksByUser(userEmail);

        // Then
        assertNotNull(foundTasks);
        assertFalse(foundTasks.isEmpty());
        assertEquals(userEmail, foundTasks.get(0).getUserEmail());
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByUser(user);
    }

    @Test
    void getTaskById_Success() {
        // Given
        Long taskId = 1L;
        Task task = new Task();
        task.setSubject("Math");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When
        Optional<Task> foundTask = taskService.getTaskById(taskId);

        // Then
        assertTrue(foundTask.isPresent());
        assertEquals("Math", foundTask.get().getSubject());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void updateTask_Success() {
        // Given
        Long taskId = 1L;
        String userEmail = "test@test.com";
        String subject = "Math";
        String grade = "5";
        String lessonUnit = "Algebra";
        String materialType = "Quiz";
        String generatedContent = "Updated content";

        User user = new User();
        user.setEmail(userEmail);

        Task existingTask = new Task();
        existingTask.setUserEmail(userEmail);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task updatedTask = taskService.updateTask(taskId, userEmail, subject, grade, lessonUnit, materialType, generatedContent);

        // Then
        assertNotNull(updatedTask);
        assertEquals(subject, updatedTask.getSubject());
        assertEquals(grade, updatedTask.getGrade());
        assertEquals(lessonUnit, updatedTask.getLessonUnit());
        assertEquals(materialType, updatedTask.getMaterialType());
        assertEquals(generatedContent, updatedTask.getGeneratedContent());
        verify(taskRepository).findById(taskId);
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // Given
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        // When
        assertDoesNotThrow(() -> taskService.deleteTask(taskId));

        // Then
        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTask_NotFound() {
        // Given
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository).existsById(taskId);
        verify(taskRepository, never()).deleteById(taskId);
    }
}
