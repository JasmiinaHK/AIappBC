package ibu.edu.ba.aiapplication.service;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import ibu.edu.ba.aiapplication.api.impl.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OpenAIService openAIService;

    private TaskService taskService;
    private Task testTask;
    private static final String TEST_EMAIL = "test@ibu.ba";
    private static final String TEST_TASK_NAME = "Test Task";

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, openAIService);
        
        testTask = new Task(
            TEST_EMAIL,
            "Math",
            "5",
            "Algebra",
            "Exercise",
            "en",
            TEST_TASK_NAME
        );
        testTask.setId(1L);
    }

    @Test
    void shouldGetAllTasks() {
        // given
        List<Task> expectedTasks = Arrays.asList(
            testTask,
            new Task(TEST_EMAIL, "Physics", "5", "Mechanics", "Quiz", "en", "Physics Task")
        );
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        // when
        List<Task> actualTasks = taskService.getAllTasks();

        // then
        assertThat(actualTasks).hasSize(2);
        assertThat(actualTasks.get(0).getName()).isEqualTo(TEST_TASK_NAME);
        assertThat(actualTasks.get(1).getName()).isEqualTo("Physics Task");
        verify(taskRepository).findAll();
    }

    @Test
    void shouldGetTasksByUserEmail() {
        // given
        List<Task> expectedTasks = List.of(testTask);
        when(taskRepository.findByUserEmail(TEST_EMAIL)).thenReturn(expectedTasks);

        // when
        List<Task> actualTasks = taskService.getTasksByUserEmail(TEST_EMAIL);

        // then
        assertThat(actualTasks).hasSize(1);
        assertThat(actualTasks.get(0).getUserEmail()).isEqualTo(TEST_EMAIL);
        assertThat(actualTasks.get(0).getName()).isEqualTo(TEST_TASK_NAME);
        verify(taskRepository).findByUserEmail(TEST_EMAIL);
    }

    @Test
    void shouldGetTaskById() {
        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // when
        Task result = taskService.getTaskById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(TEST_TASK_NAME);
        assertThat(result.getUserEmail()).isEqualTo(TEST_EMAIL);
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taskService.getTaskById(999L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Task not found");
    }

    @Test
    void shouldCreateTask() {
        // given
        Task newTask = new Task(
            TEST_EMAIL,
            "Math",
            "5",
            "Algebra",
            "Exercise",
            "en",
            "New Task"
        );
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        // when
        Task result = taskService.createTask(newTask);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getName()).isEqualTo("New Task");
        verify(taskRepository).save(newTask);
    }

    @Test
    void shouldUpdateTask() {
        // given
        Task updateRequest = new Task(
            TEST_EMAIL,
            "Updated Math",
            "6",
            "Geometry",
            "Quiz",
            "en",
            "Updated Task"
        );
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Task result = taskService.updateTask(1L, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSubject()).isEqualTo("Updated Math");
        assertThat(result.getGrade()).isEqualTo("6");
        assertThat(result.getLessonUnit()).isEqualTo("Geometry");
        assertThat(result.getMaterialType()).isEqualTo("Quiz");
        assertThat(result.getName()).isEqualTo("Updated Task");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTaskWithDifferentUser() {
        // given
        Task updateRequest = new Task(
            "different@ibu.ba",
            "Math",
            "5",
            "Algebra",
            "Exercise",
            "en",
            "Different Task"
        );
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // when/then
        assertThatThrownBy(() -> taskService.updateTask(1L, updateRequest))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Cannot update task owned by another user");
    }

    @Test
    void shouldDeleteTask() {
        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(any(Task.class));

        // when
        taskService.deleteTask(1L);

        // then
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(testTask);
    }

    @Test
    void shouldGenerateContent() {
        // given
        String expectedContent = "Generated content for Math, grade 5";
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(openAIService.generateContent(anyString())).thenReturn(expectedContent);
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task savedTask = i.getArgument(0);
            assertThat(savedTask.getContent()).isEqualTo(expectedContent);
            return savedTask;
        });

        // when
        Task result = taskService.generateContent(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(expectedContent);
        verify(openAIService).generateContent(anyString());
        verify(taskRepository).save(any(Task.class));

        // Verify prompt includes all fields
        verify(openAIService).generateContent(argThat(prompt -> 
            prompt.contains(testTask.getSubject()) &&
            prompt.contains(testTask.getGrade()) &&
            prompt.contains(testTask.getLessonUnit()) &&
            prompt.contains(testTask.getMaterialType()) &&
            prompt.contains(testTask.getName())
        ));
    }
}
