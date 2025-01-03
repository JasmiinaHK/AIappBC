package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Create new task")
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @Operation(summary = "Update task")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        // Verify task exists (will throw if not found)
        taskService.getTaskById(id);
        task.setId(id);
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        // Verify task exists (will throw if not found)
        taskService.getTaskById(id);
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get tasks by user email")
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Task>> getTasksByUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(taskService.getTasksByUserEmail(email));
    }

    @Operation(summary = "Generate task content")
    @PostMapping("/{id}/generate")
    public ResponseEntity<Task> generateTaskContent(@PathVariable String id) {
        return ResponseEntity.ok(taskService.generateContent(id));
    }
}
