package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);
    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    // Kreiranje novog materijala
    @PostMapping
    public ResponseEntity<?> createMaterial(@RequestBody Material material) {
        try {
            logger.info("Creating new material: {}", material);
            Material createdMaterial = materialService.createMaterial(material);
            logger.info("Material created successfully: {}", createdMaterial);
            return ResponseEntity.ok(createdMaterial);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating material: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating material: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to create material: " + e.getMessage()));
        }
    }

    // Generisanje sadržaja za materijal
    @PostMapping("/{id}/generate")
    public ResponseEntity<?> generateContent(
            @PathVariable Long id,
            @RequestBody Material material) {
        try {
            logger.info("Received generate content request for ID: {} with data: {}", id, material);
            
            if (material == null) {
                logger.error("Material data is null");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Material data is required"));
            }
            
            if (material.getUserEmail() == null || material.getUserEmail().isEmpty()) {
                logger.error("User email is missing");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User email is required"));
            }

            logger.info("Generating content for material ID: {} and user: {}", id, material.getUserEmail());
            Material updatedMaterial = materialService.generateContent(id, material.getUserEmail(), material);
            logger.info("Content generated successfully for material ID: {}", id);
            return ResponseEntity.ok(updatedMaterial);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error generating content: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error generating content: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate content: " + e.getMessage()));
        }
    }

    // Dobavljanje svih materijala
    @GetMapping
    public ResponseEntity<?> getMaterials(
            @RequestParam String userEmail,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String lessonUnit,
            @RequestParam(required = false) String materialType,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            logger.info("Received GET request for materials with parameters: userEmail={}, subject={}, grade={}, lessonUnit={}, materialType={}, language={}, sortBy={}, sortDirection={}, page={}, size={}", 
                userEmail, subject, grade, lessonUnit, materialType, language, sortBy, sortDirection, page, size);
            Page<Material> materials = materialService.getMaterials(
                userEmail,
                subject,
                grade,
                lessonUnit,
                materialType,
                language,
                sortBy,
                sortDirection,
                PageRequest.of(page, size)
            );
            
            return ResponseEntity.ok(materials);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error getting materials: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting materials: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get materials: " + e.getMessage()));
        }
    }

    // Dobavljanje materijala po email-u korisnika
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getMaterialsByUserEmail(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Getting materials for user: {}", email);
            Page<Material> materials = materialService.getMaterialsByUserEmail(email, page, size);
            return ResponseEntity.ok(materials);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error getting materials by user email: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting materials: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get materials: " + e.getMessage()));
        }
    }

    // Dobavljanje materijala po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<?> getMaterialById(@PathVariable Long id) {
        try {
            logger.info("Getting material by ID: {}", id);
            return materialService.getMaterialById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            logger.error("Validation error getting material by ID: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting material: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get material: " + e.getMessage()));
        }
    }

    // Ažuriranje materijala
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaterial(@PathVariable Long id, @RequestBody Material material) {
        try {
            Material updatedMaterial = materialService.updateMaterial(id, material);
            return ResponseEntity.ok(updatedMaterial);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating material: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating material {}: ", id, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to update material: " + e.getMessage()));
        }
    }

    // Brisanje materijala
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaterial(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        try {
            logger.info("Deleting material with ID: {} by user: {}", id, userEmail);
            materialService.deleteMaterial(id, userEmail);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Validation error deleting material: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting material: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to delete material: " + e.getMessage()));
        }
    }
}
