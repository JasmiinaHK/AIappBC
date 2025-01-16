package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.rest.configuration.OpenAIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final List<String> AVAILABLE_SUBJECTS = Arrays.asList(
        "Matematika",
        "Biologija",
        "Geografija"
    );

    @Autowired
    public SubjectController(OpenAIConfiguration openAIConfiguration) {
        this.openAIConfiguration = openAIConfiguration;
    }

    @GetMapping
    public ResponseEntity<List<String>> getSubjects() {
        logger.info("Fetching available subjects");
        return ResponseEntity.ok(AVAILABLE_SUBJECTS);
    }

    @GetMapping("/{subject}/lesson-units")
    public ResponseEntity<List<String>> getLessonUnits(@PathVariable String subject) {
        logger.info("Generating lesson units for subject: {}", subject);
        
        if (!AVAILABLE_SUBJECTS.contains(subject)) {
            logger.warn("Invalid subject requested: {}", subject);
            return ResponseEntity.badRequest().build();
        }

        try {
            String lessonUnitsStr = openAIConfiguration.generateLessonUnits(subject);
            List<String> lessonUnits = Arrays.asList(lessonUnitsStr.split(",\\s*"));
            logger.info("Generated {} lesson units for {}", lessonUnits.size(), subject);
            return ResponseEntity.ok(lessonUnits);
        } catch (Exception e) {
            logger.error("Error generating lesson units for {}: {}", subject, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
