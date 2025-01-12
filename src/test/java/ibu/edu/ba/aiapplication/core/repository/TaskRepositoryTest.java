package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.config.TestConfig;
import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        taskRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.flush();
        
        // Create test user
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setName("Test User");
        testUser = userRepository.save(testUser);
        entityManager.flush();
    }

    @Test
    void createAndRetrieveTask() {
        // Given
        Task task = new Task();
        task.setUserEmail("test@test.com");
        task.setSubject("Math");
        task.setGrade("5");
        task.setLessonUnit("Algebra");
        task.setMaterialType("Quiz");
        task.setUser(testUser);
        task.setGeneratedContent("Generated content");

        // When
        Task savedTask = taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();
        Task retrievedTask = taskRepository.findById(savedTask.getId()).orElse(null);

        // Then
        assertNotNull(retrievedTask);
        assertEquals("Math", retrievedTask.getSubject());
        assertEquals("5", retrievedTask.getGrade());
        assertEquals("Algebra", retrievedTask.getLessonUnit());
        assertEquals("Quiz", retrievedTask.getMaterialType());
        assertEquals("Generated content", retrievedTask.getGeneratedContent());
        assertEquals(testUser.getId(), retrievedTask.getUser().getId());
    }

    @Test
    void findByUser() {
        // Given
        Task task1 = new Task();
        task1.setUserEmail("test@test.com");
        task1.setSubject("Math");
        task1.setGrade("5");
        task1.setLessonUnit("Algebra");
        task1.setMaterialType("Quiz");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setUserEmail("test@test.com");
        task2.setSubject("Physics");
        task2.setGrade("5");
        task2.setLessonUnit("Mechanics");
        task2.setMaterialType("Quiz");
        task2.setUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Task> tasks = taskRepository.findByUser(testUser);

        // Then
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void findByUserId() {
        // Given
        Task task1 = new Task();
        task1.setUserEmail("test@test.com");
        task1.setSubject("Math");
        task1.setGrade("5");
        task1.setLessonUnit("Algebra");
        task1.setMaterialType("Quiz");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setUserEmail("test@test.com");
        task2.setSubject("Physics");
        task2.setGrade("5");
        task2.setLessonUnit("Mechanics");
        task2.setMaterialType("Quiz");
        task2.setUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Task> tasks = taskRepository.findByUserId(testUser.getId());

        // Then
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void deleteTask() {
        // Given
        Task task = new Task();
        task.setUserEmail("test@test.com");
        task.setSubject("Math");
        task.setGrade("5");
        task.setLessonUnit("Algebra");
        task.setMaterialType("Quiz");
        task.setUser(testUser);
        Task savedTask = taskRepository.save(task);
        entityManager.flush();

        // When
        taskRepository.deleteById(savedTask.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        assertFalse(taskRepository.findById(savedTask.getId()).isPresent());
    }
}
