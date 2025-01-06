package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.repository.TaskRepository;
import ibu.edu.ba.aiapplication.api.impl.OpenAIService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final OpenAIService openAIService;

    public TaskService(TaskRepository taskRepository, OpenAIService openAIService) {
        this.taskRepository = taskRepository;
        this.openAIService = openAIService;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUserEmail(String email) {
        return taskRepository.findByUserEmail(email);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);
        
        // Only update if the user email matches
        if (!task.getUserEmail().equals(updatedTask.getUserEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot update task owned by another user");
        }

        task.setSubject(updatedTask.getSubject());
        task.setGrade(updatedTask.getGrade());
        task.setLessonUnit(updatedTask.getLessonUnit());
        task.setMaterialType(updatedTask.getMaterialType());
        task.setLanguage(updatedTask.getLanguage());
        task.setName(updatedTask.getName());
        if (updatedTask.getContent() != null) {
            task.setContent(updatedTask.getContent());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    public Task generateContent(Long id) {
        Task task = getTaskById(id);
        String prompt = String.format(
            "Generate educational content for:\nSubject: %s\nGrade: %s\nUnit: %s\nType: %s\nName: %s",
            task.getSubject(),
            task.getGrade(),
            task.getLessonUnit(),
            task.getMaterialType(),
            task.getName()
        );
        
        String generatedContent = openAIService.generateContent(prompt);
        task.setContent(generatedContent);
        
        return taskRepository.save(task);
    }
}
