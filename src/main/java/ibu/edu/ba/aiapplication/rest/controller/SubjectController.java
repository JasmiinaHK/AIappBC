package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.rest.configuration.OpenAIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = {"http://localhost:3000", "https://aiappfc.onrender.com"})
public class SubjectController {
    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);
    private final OpenAIConfiguration openAIConfiguration;

    public SubjectController(OpenAIConfiguration openAIConfiguration) {
        this.openAIConfiguration = openAIConfiguration;
    }

    @GetMapping
    public ResponseEntity<List<String>> getSubjects() {
        List<String> subjects = Arrays.asList("Matematika", "Biologija", "Geografija");
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{subject}/lesson-units")
    public ResponseEntity<?> getLessonUnits(@PathVariable String subject) {
        logger.info("Received request to generate lesson units for subject: {}", subject);

        if (subject == null || subject.trim().isEmpty()) {
            logger.error("Subject is null or empty");
            return ResponseEntity.badRequest()
                .body("Subject is required");
        }

        try {
            String lessonUnitsStr = openAIConfiguration.generateLessonUnits(subject);
            List<String> lessonUnits = Arrays.asList(lessonUnitsStr.split(",\\s*"));
            logger.info("Generated {} lesson units for {}", lessonUnits.size(), subject);
            return ResponseEntity.ok(lessonUnits);
        } catch (Exception e) {
            logger.error("Error generating lesson units for subject {}: {}", subject, e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Failed to generate lesson units: " + e.getMessage());
        }
    }
}
