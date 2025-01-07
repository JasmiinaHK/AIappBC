package ibu.edu.ba.aiapplication.repository;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaterialRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MaterialRepository materialRepository;

    private Material testMaterial;
    private static final Long TEST_TASK_ID = 1L;

    @BeforeEach
    void setUp() {
        testMaterial = new Material("Test content", TEST_TASK_ID);
        
        // Clear and save test data
        entityManager.clear();
        entityManager.persist(testMaterial);
        entityManager.flush();
    }

    @Test
    void findById_WhenExists_ShouldReturnMaterial() {
        Optional<Material> found = materialRepository.findById(testMaterial.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Test content");
        assertThat(found.get().getTaskId()).isEqualTo(TEST_TASK_ID);
    }

    @Test
    void save_ShouldPersistMaterial() {
        Material newMaterial = new Material("New content", 2L);
        Material saved = materialRepository.save(newMaterial);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("New content");
        assertThat(saved.getTaskId()).isEqualTo(2L);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveMaterial() {
        materialRepository.deleteById(testMaterial.getId());
        
        Optional<Material> deleted = materialRepository.findById(testMaterial.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void findByTaskId_ShouldReturnMaterials() {
        List<Material> materials = materialRepository.findByTaskId(TEST_TASK_ID);
        
        assertThat(materials).isNotEmpty();
        assertThat(materials).allMatch(m -> m.getTaskId().equals(TEST_TASK_ID));
    }

    @Test
    void save_ShouldUpdateTimestamps() {
        // Save initial material
        Material material = new Material("Initial content", TEST_TASK_ID);
        Material saved = materialRepository.save(material);
        
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        
        // Update the material
        saved.setContent("Updated content");
        Material updated = materialRepository.save(saved);
        
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getCreatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(updated.getUpdatedAt()).isNotEqualTo(saved.getUpdatedAt());
    }
}
