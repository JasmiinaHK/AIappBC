package ibu.edu.ba.aiapplication.restConfiguration.controller;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "APIs for managing educational tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Test OpenAI integration")
    @GetMapping("/test-ai")
    public ResponseEntity<String> testAI() {
        try {
            logger.info("Starting OpenAI test");
            
            Task testTask = new Task();
            testTask.setSubject("Mathematics");
            testTask.setGrade("5th");
            testTask.setLessonUnit("Basic Algebra");
            testTask.setMaterialType("Exercise");
            
            logger.info("Saving test task");
            Task savedTask = taskService.saveTask(testTask);
            logger.info("Test task saved with ID: {}", savedTask.getId());
            
            logger.info("Generating content for task");
            Task generatedTask = taskService.generateContent(savedTask.getId());
            logger.info("Content generated successfully");
            
            return ResponseEntity.ok(generatedTask.getContent());
        } catch (Exception e) {
            logger.error("Error in test-ai endpoint", e);
            throw e;
        }
    }

    @Operation(summary = "Test endpoint")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Task Controller is working!");
    }

    @Operation(summary = "Get tasks by user email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the tasks"),
        @ApiResponse(responseCode = "404", description = "No tasks found")
    })
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Task>> getTasksByEmail(
            @Parameter(description = "User email") @PathVariable String email) {
        List<Task> tasks = taskService.getTasksByUserEmail(email);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @Operation(summary = "Generate content for a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Content generated"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PostMapping("/{id}/generate")
    public ResponseEntity<Task> generateTaskContent(
            @Parameter(description = "Task ID") @PathVariable String id) {
        Task task = taskService.generateContent(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
