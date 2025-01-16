package ibu.edu.ba.aiapplication.core.service;

import com.theokanning.openai.service.OpenAiService;
import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import ibu.edu.ba.aiapplication.rest.configuration.OpenAIConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MaterialService {

    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);
    private final MaterialRepository materialRepository;
    private final OpenAIConfiguration openAIConfiguration;

    @Autowired
    public MaterialService(MaterialRepository materialRepository, OpenAIConfiguration openAIConfiguration) {
        this.materialRepository = materialRepository;
        this.openAIConfiguration = openAIConfiguration;
    }

    public Material createMaterial(Material material) {
        logger.info("Creating new material: {}", material);
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        if (material.getUserEmail() == null || material.getUserEmail().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }
        try {
            Material savedMaterial = materialRepository.save(material);
            logger.info("Material created successfully: {}", savedMaterial);
            return savedMaterial;
        } catch (Exception e) {
            logger.error("Error creating material: ", e);
            throw new IllegalArgumentException("Failed to create material: " + e.getMessage(), e);
        }
    }

    public Material generateContent(Long materialId, String userEmail, Material updatedMaterial) {
        try {
            logger.info("Starting content generation for material ID: {} and user: {}", materialId, userEmail);
            logger.info("Updated material data: {}", updatedMaterial);
            
            logger.info("Finding existing material...");
            Material existingMaterial = materialRepository.findById(materialId)
                    .orElseThrow(() -> new RuntimeException("Material not found with ID: " + materialId));
            logger.info("Found existing material: {}", existingMaterial);
            
            if (!existingMaterial.getUserEmail().equals(userEmail)) {
                logger.error("Authorization failed. Material owner: {}, Requester: {}", 
                    existingMaterial.getUserEmail(), userEmail);
                throw new RuntimeException("User is not authorized to modify this material");
            }
            
            // Update material with the latest data
            logger.info("Updating material fields...");
            existingMaterial.setSubject(updatedMaterial.getSubject());
            existingMaterial.setGrade(updatedMaterial.getGrade());
            existingMaterial.setLessonUnit(updatedMaterial.getLessonUnit());
            existingMaterial.setMaterialType(updatedMaterial.getMaterialType());
            existingMaterial.setLanguage(updatedMaterial.getLanguage());
            logger.info("Material fields updated: {}", existingMaterial);
            
            // Generate content using OpenAI
            logger.info("Calling OpenAI to generate content...");
            String generatedContent = openAIConfiguration.generateContent(existingMaterial);
            logger.info("OpenAI content generated successfully. Length: {} characters", 
                generatedContent != null ? generatedContent.length() : 0);
            
            existingMaterial.setGeneratedContent(generatedContent);
            
            logger.info("Saving updated material to database...");
            Material savedMaterial = materialRepository.save(existingMaterial);
            logger.info("Material saved successfully with generated content");
            
            return savedMaterial;
        } catch (Exception e) {
            logger.error("Error generating content for material {}. Error: {}", materialId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate content: " + e.getMessage(), e);
        }
    }

    public Page<Material> getMaterials(
            String userEmail,
            String subject,
            String grade,
            String lessonUnit,
            String materialType,
            String language,
            String sortBy,
            String sortDirection,
            Pageable pageable) {
        
        Material.MaterialSpecification spec = new Material.MaterialSpecification();
        
        // Add base filter for user email
        spec.add(new Material.SearchCriteria("userEmail", Material.SearchOperation.EQUALS, userEmail));
        
        // Add optional filters
        if (subject != null && !subject.isEmpty()) {
            spec.add(new Material.SearchCriteria("subject", Material.SearchOperation.LIKE, subject));
        }
        if (grade != null && !grade.isEmpty()) {
            spec.add(new Material.SearchCriteria("grade", Material.SearchOperation.EQUALS, grade));
        }
        if (lessonUnit != null && !lessonUnit.isEmpty()) {
            spec.add(new Material.SearchCriteria("lessonUnit", Material.SearchOperation.LIKE, lessonUnit));
        }
        if (materialType != null && !materialType.isEmpty()) {
            spec.add(new Material.SearchCriteria("materialType", Material.SearchOperation.EQUALS, materialType));
        }
        if (language != null && !language.isEmpty()) {
            spec.add(new Material.SearchCriteria("language", Material.SearchOperation.EQUALS, language));
        }

        // Create sort
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        
        // Create new pageable with sort
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sort
        );

        return materialRepository.findAll(spec, pageRequest);
    }

    public Optional<Material> getMaterialById(Long id) {
        try {
            return materialRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error getting material by ID {}: ", id, e);
            throw new RuntimeException("Failed to get material: " + e.getMessage(), e);
        }
    }

    public Page<Material> getMaterialsByUserEmail(String email, int page, int size) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
            return materialRepository.findByUserEmail(email, pageRequest);
        } catch (Exception e) {
            logger.error("Error getting materials for user {}: ", email, e);
            throw new RuntimeException("Failed to get materials: " + e.getMessage(), e);
        }
    }

    public Material updateMaterial(Long id, Material material) {
        try {
            Material existingMaterial = materialRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));
            
            if (!existingMaterial.getUserEmail().equals(material.getUserEmail())) {
                throw new RuntimeException("User is not authorized to modify this material");
            }
            
            existingMaterial.setSubject(material.getSubject());
            existingMaterial.setGrade(material.getGrade());
            existingMaterial.setLessonUnit(material.getLessonUnit());
            existingMaterial.setMaterialType(material.getMaterialType());
            existingMaterial.setLanguage(material.getLanguage());
            existingMaterial.setUserEmail(material.getUserEmail());
            if (material.getGeneratedContent() != null) {
                existingMaterial.setGeneratedContent(material.getGeneratedContent());
            }
            
            return materialRepository.save(existingMaterial);
        } catch (Exception e) {
            logger.error("Error updating material {}: ", id, e);
            throw new RuntimeException("Failed to update material: " + e.getMessage(), e);
        }
    }

    public void deleteMaterial(Long id, String userEmail) {
        try {
            Material material = materialRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));
            
            if (!material.getUserEmail().equals(userEmail)) {
                throw new RuntimeException("User is not authorized to delete this material");
            }
            
            materialRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting material {}: ", id, e);
            throw new RuntimeException("Failed to delete material: " + e.getMessage(), e);
        }
    }
}
