package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/user/{email}")
    @Operation(summary = "Get tasks by user email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
        @ApiResponse(responseCode = "403", description = "User not found in database")
    })
    public ResponseEntity<?> getTasksByUser(@PathVariable String email) {
        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User with email " + email + " not found in database");
        }
        List<Task> tasks = taskService.getTasksByUserEmail(email);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created task"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "User not found in database")
    })
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        if (!userRepository.existsByEmail(task.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User with email " + task.getUserEmail() + " not found in database");
        }
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated task"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "User not found in database")
    })
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        if (!userRepository.existsByEmail(task.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User with email " + task.getUserEmail() + " not found in database");
        }
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted task"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/generate")
    @Operation(summary = "Generate content for a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully generated content"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> generateContent(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.generateContent(id));
    }
}
