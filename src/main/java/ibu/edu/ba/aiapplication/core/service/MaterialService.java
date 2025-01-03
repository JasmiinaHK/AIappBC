package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import ibu.edu.ba.aiapplication.api.impl.OpenAIService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final OpenAIService openAIService;

    public MaterialService(MaterialRepository materialRepository, OpenAIService openAIService) {
        this.materialRepository = materialRepository;
        this.openAIService = openAIService;
    }

    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }

    public List<Material> getMaterialsByUser(Long userId) {
        return materialRepository.findByUserId(userId);
    }

    public List<Material> getMaterialsByType(String materialType) {
        return materialRepository.findByMaterialType(materialType);
    }

    public List<Material> getMaterialsBySubject(String subject) {
        return materialRepository.findBySubject(subject);
    }

    public List<Material> getMaterialsByGrade(String grade) {
        return materialRepository.findByGrade(grade);
    }

    public Material createMaterial(Material material, User user) {
        material.setId(UUID.randomUUID().toString());
        material.setUser(user);
        return materialRepository.save(material);
    }

    public Material generateContent(Material material, User user) {
        String generatedContent = openAIService.generateTaskContent(
            material.getSubject(),
            material.getGrade(),
            material.getLessonUnit(),
            material.getMaterialType()
        );
        material.setContent(generatedContent);
        return createMaterial(material, user);
    }

    public Material getMaterialById(String id) {
        return materialRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }

    public Material updateMaterial(String id, Material updatedMaterial) {
        Material material = getMaterialById(id);
        
        if (updatedMaterial.getSubject() != null) {
            material.setSubject(updatedMaterial.getSubject());
        }
        if (updatedMaterial.getGrade() != null) {
            material.setGrade(updatedMaterial.getGrade());
        }
        if (updatedMaterial.getLessonUnit() != null) {
            material.setLessonUnit(updatedMaterial.getLessonUnit());
        }
        if (updatedMaterial.getMaterialType() != null) {
            material.setMaterialType(updatedMaterial.getMaterialType());
        }
        if (updatedMaterial.getContent() != null) {
            material.setContent(updatedMaterial.getContent());
        }
        if (updatedMaterial.getLanguage() != null) {
            material.setLanguage(updatedMaterial.getLanguage());
        }

        return materialRepository.save(material);
    }

    public void deleteMaterial(String id) {
        if (!materialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found");
        }
        materialRepository.deleteById(id);
    }
}
