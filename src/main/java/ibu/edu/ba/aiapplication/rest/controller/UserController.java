package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/debug")
    @Operation(summary = "Debug: Get all users and check specific emails")
    public ResponseEntity<Map<String, Object>> debugUsers() {
        Map<String, Object> debug = new HashMap<>();
        List<User> allUsers = userRepository.findAll();
        
        debug.put("all_users", allUsers);
        debug.put("total_users", allUsers.size());
        debug.put("jasmina_exists", userRepository.existsByEmail("jasmina@ibu.ba"));
        debug.put("profesor_exists", userRepository.existsByEmail("profesor@ibu.ba"));
        
        return ResponseEntity.ok(debug);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{email}")
    @Operation(summary = "Update a user")
    public ResponseEntity<User> updateUser(@PathVariable String email, @Valid @RequestBody User user) {
        if (!email.equals(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email in path must match email in body");
        }
        
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        existingUser.setName(user.getName());
        return ResponseEntity.ok(userRepository.save(existingUser));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteByEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{email}")
    @Operation(summary = "Check if user exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@PathVariable String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userRepository.existsByEmail(email));
        return ResponseEntity.ok(response);
    }
}
