package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final OpenAIService openAIService;

    public TaskService(TaskRepository taskRepository, OpenAIService openAIService) {
        this.taskRepository = taskRepository;
        this.openAIService = openAIService;
    }

    public List<Task> getTasksByUserEmail(String email) {
        return taskRepository.findByUserEmail(email);
    }

    public Task saveTask(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        return taskRepository.save(task);
    }

    public Task generateContent(String taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        String generatedContent = openAIService.generateTaskContent(
            task.getSubject(),
            task.getGrade(),
            task.getLessonUnit(),
            task.getMaterialType()
        );

        task.setContent(generatedContent);
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}
