package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    // Dodajemo metodu getAllMaterials koja vraća sve materijale
    public List<Material> getAllMaterials() {
        return materialRepository.findAll(); // Poziva findAll() metodu iz repository-ja
    }

    public List<Material> getMaterialsByTaskId(Long taskId) {
        return materialRepository.findByTaskId(taskId);
    }

    public Material getMaterialById(Long id) {
        return materialRepository.findById(id).orElse(null);
    }

    @Transactional
    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }

    @Transactional
    public Material updateMaterial(Long id, Material material) {
        if (!materialRepository.existsById(id)) {
            return null;
        }
        material.setId(id); // Ensure the ID is set correctly
        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }

    // Ostale metode...
}
