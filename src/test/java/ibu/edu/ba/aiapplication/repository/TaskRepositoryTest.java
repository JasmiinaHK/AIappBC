package ibu.edu.ba.aiapplication.repository;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private static final String TEST_TASK_NAME = "Test Task";

    @BeforeEach
    void setUp() {
        // Clear previous data
        taskRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test user
        testUser = userRepository.save(new User("test@ibu.ba", "Test User"));
    }

    @Test
    void shouldSaveTask() {
        // given
        Task task = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", TEST_TASK_NAME);

        // when
        Task savedTask = taskRepository.save(task);

        // then
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getUserEmail()).isEqualTo(testUser.getEmail());
        assertThat(savedTask.getName()).isEqualTo(TEST_TASK_NAME);
        assertThat(savedTask.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindTasksByUserEmail() {
        // given
        Task task1 = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "Math Task");
        Task task2 = new Task(testUser.getEmail(), "Physics", "5", "Mechanics", "Quiz", "en", "Physics Task");
        taskRepository.save(task1);
        taskRepository.save(task2);

        // when
        List<Task> tasks = taskRepository.findByUserEmail(testUser.getEmail());

        // then
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getUserEmail)
                        .containsOnly(testUser.getEmail());
    }

    @Test
    void shouldFindTasksByUserEmailOrderedByCreatedAt() {
        // given
        Task task1 = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "First Task");
        Task task2 = new Task(testUser.getEmail(), "Physics", "5", "Mechanics", "Quiz", "en", "Second Task");
        taskRepository.save(task1);
        taskRepository.save(task2);

        // when
        List<Task> tasks = taskRepository.findByUserEmailOrderByCreatedAtDesc(testUser.getEmail());

        // then
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getCreatedAt()).isAfterOrEqualTo(tasks.get(1).getCreatedAt());
    }

    @Test
    void shouldNotFindTasksForNonExistentUser() {
        // when
        List<Task> tasks = taskRepository.findByUserEmail("nonexistent@ibu.ba");

        // then
        assertThat(tasks).isEmpty();
    }

    @Test
    void shouldUpdateTask() {
        // given
        Task task = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "Initial Name");
        Task savedTask = taskRepository.save(task);

        // when
        savedTask.setName("Updated Name");
        savedTask.setSubject("Updated Math");
        Task updatedTask = taskRepository.save(savedTask);

        // then
        assertThat(updatedTask.getName()).isEqualTo("Updated Name");
        assertThat(updatedTask.getSubject()).isEqualTo("Updated Math");
        assertThat(updatedTask.getUpdatedAt()).isNotNull();
        assertThat(updatedTask.getUpdatedAt()).isAfterOrEqualTo(updatedTask.getCreatedAt());
    }

    @Test
    void shouldDeleteTask() {
        // given
        Task task = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "Task to Delete");
        Task savedTask = taskRepository.save(task);

        // when
        taskRepository.deleteById(savedTask.getId());

        // then
        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }

    @Test
    void shouldFindTasksByUserEmailAndSubject() {
        // given
        Task task1 = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "Math Task 1");
        Task task2 = new Task(testUser.getEmail(), "Math", "5", "Geometry", "Quiz", "en", "Math Task 2");
        Task task3 = new Task(testUser.getEmail(), "Physics", "5", "Mechanics", "Exercise", "en", "Physics Task");
        taskRepository.saveAll(List.of(task1, task2, task3));

        // when
        List<Task> mathTasks = taskRepository.findByUserEmailAndSubject(testUser.getEmail(), "Math");

        // then
        assertThat(mathTasks).hasSize(2);
        assertThat(mathTasks).extracting(Task::getSubject)
                           .containsOnly("Math");
    }

    @Test
    void shouldFindTasksByUserEmailAndSubjectOrdered() {
        // given
        Task task1 = new Task(testUser.getEmail(), "Math", "5", "Algebra", "Exercise", "en", "Math Task 1");
        Task task2 = new Task(testUser.getEmail(), "Math", "5", "Geometry", "Quiz", "en", "Math Task 2");
        taskRepository.save(task1);
        taskRepository.save(task2);

        // when
        List<Task> mathTasks = taskRepository.findByUserEmailAndSubjectOrderByCreatedAtDesc(testUser.getEmail(), "Math");

        // then
        assertThat(mathTasks).hasSize(2);
        assertThat(mathTasks).extracting(Task::getSubject)
                           .containsOnly("Math");
        assertThat(mathTasks.get(0).getCreatedAt()).isAfterOrEqualTo(mathTasks.get(1).getCreatedAt());
    }
}
