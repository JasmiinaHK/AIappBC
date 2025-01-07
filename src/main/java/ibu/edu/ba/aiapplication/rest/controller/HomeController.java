package ibu.edu.ba.aiapplication.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Welcome to AI Education API");
        response.put("version", "1.0.0");
        response.put("endpoints", new String[]{
            "/api/tasks",
            "/api/users",
            "/api/materials",
            "/swagger-ui/index.html"
        });
        return ResponseEntity.ok(response);
    }
}
