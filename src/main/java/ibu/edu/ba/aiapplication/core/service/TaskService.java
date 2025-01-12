package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Kreiraj novi zadatak
    public Task createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getUserEmail() == null || task.getUserEmail().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }

        User user = userRepository.findByEmail(task.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + task.getUserEmail()));
        task.setUser(user);

        return taskRepository.save(task);
    }

    // Dohvati sve zadatke korisnika po ID-u korisnika
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Dohvati sve zadatke korisnika po emailu
    public List<Task> getTasksByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return taskRepository.findByUser(user);
    }

    // Dohvati zadatak po ID-u
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Ažuriraj zadatak sa generisanim sadržajem
    public Task updateTask(Long id, String userEmail, String subject, String grade, String lessonUnit, String materialType, String generatedContent) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        task.setUserEmail(userEmail);
        task.setUser(user);
        task.setSubject(subject);
        task.setGrade(grade);
        task.setLessonUnit(lessonUnit);
        task.setMaterialType(materialType);
        task.setGeneratedContent(generatedContent);

        return taskRepository.save(task);
    }

    // Obriši zadatak
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }
}
