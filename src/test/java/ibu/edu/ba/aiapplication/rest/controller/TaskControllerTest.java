package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

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

        Task task = new Task();
        task.setUserEmail(userEmail);
        task.setSubject(subject);
        task.setGrade(grade);
        task.setLessonUnit(lessonUnit);
        task.setMaterialType(materialType);
        task.setGeneratedContent(generatedContent);

        when(taskService.createTask(task)).thenReturn(task);

        // When
        ResponseEntity<?> response = taskController.createTask(task);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Task);
        Task responseTask = (Task) response.getBody();
        assertEquals(subject, responseTask.getSubject());
        verify(taskService).createTask(task);
    }

    @Test
    void getTasksByUser_Success() {
        // Given
        String userEmail = "test@test.com";
        Task task = new Task();
        task.setUserEmail(userEmail);
        task.setSubject("Math");
        task.setGrade("5");
        task.setLessonUnit("Algebra");
        task.setMaterialType("Quiz");
        List<Task> tasks = Arrays.asList(task);

        when(taskService.getTasksByUser(userEmail)).thenReturn(tasks);

        // When
        ResponseEntity<?> response = taskController.getTasksByUser(userEmail);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<?> responseList = (List<?>) response.getBody();
        assertEquals(1, responseList.size());
        assertTrue(responseList.get(0) instanceof Task);
        verify(taskService).getTasksByUser(userEmail);
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

        Task task = new Task();
        task.setUserEmail(userEmail);
        task.setSubject(subject);
        task.setGrade(grade);
        task.setLessonUnit(lessonUnit);
        task.setMaterialType(materialType);
        task.setGeneratedContent(generatedContent);

        when(taskService.updateTask(eq(taskId), eq(userEmail), eq(subject), eq(grade), eq(lessonUnit), eq(materialType), eq(generatedContent)))
                .thenReturn(task);

        // When
        ResponseEntity<?> response = taskController.updateTask(taskId, userEmail, subject, grade, lessonUnit, materialType, generatedContent);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Task);
        Task responseTask = (Task) response.getBody();
        assertEquals(subject, responseTask.getSubject());
        assertEquals(generatedContent, responseTask.getGeneratedContent());
        verify(taskService).updateTask(taskId, userEmail, subject, grade, lessonUnit, materialType, generatedContent);
    }

    @Test
    void updateTask_NotFound() {
        // Given
        Long taskId = 1L;
        String userEmail = "test@test.com";
        String subject = "Math";
        String grade = "5";
        String lessonUnit = "Algebra";
        String materialType = "Quiz";
        String generatedContent = "Updated content";

        when(taskService.updateTask(eq(taskId), eq(userEmail), eq(subject), eq(grade), eq(lessonUnit), eq(materialType), eq(generatedContent)))
                .thenThrow(new IllegalArgumentException("Task not found"));

        // When
        ResponseEntity<?> response = taskController.updateTask(taskId, userEmail, subject, grade, lessonUnit, materialType, generatedContent);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taskService).updateTask(taskId, userEmail, subject, grade, lessonUnit, materialType, generatedContent);
    }

    @Test
    void deleteTask_Success() {
        // Given
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // When
        ResponseEntity<?> response = taskController.deleteTask(taskId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).deleteTask(taskId);
    }

    @Test
    void deleteTask_NotFound() {
        // Given
        Long taskId = 1L;
        doThrow(new IllegalArgumentException("Task not found")).when(taskService).deleteTask(taskId);

        // When
        ResponseEntity<?> response = taskController.deleteTask(taskId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taskService).deleteTask(taskId);
    }
}
