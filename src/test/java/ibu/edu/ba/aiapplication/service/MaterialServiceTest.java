package ibu.edu.ba.aiapplication.service;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    private MaterialService materialService;

    private Material testMaterial;

    @BeforeEach
    void setUp() {
        materialService = new MaterialService(materialRepository);
        
        testMaterial = new Material();
        testMaterial.setId(1L);
        testMaterial.setContent("Test Content");
        testMaterial.setTaskId(1L);
    }

    @Test
    void getAllMaterials_ReturnsAllMaterials() {
        when(materialRepository.findAll())
            .thenReturn(Arrays.asList(testMaterial));

        List<Material> result = materialService.getAllMaterials();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMaterial.getId(), result.get(0).getId());
        assertEquals(testMaterial.getContent(), result.get(0).getContent());
        assertEquals(testMaterial.getTaskId(), result.get(0).getTaskId());

        verify(materialRepository).findAll();
    }

    @Test
    void getAllMaterials_ReturnsEmptyList() {
        when(materialRepository.findAll())
            .thenReturn(Collections.emptyList());

        List<Material> result = materialService.getAllMaterials();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(materialRepository).findAll();
    }

    @Test
    void getMaterialsByTaskId_ReturnsMaterials() {
        when(materialRepository.findByTaskId(1L))
            .thenReturn(Arrays.asList(testMaterial));

        List<Material> result = materialService.getMaterialsByTaskId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMaterial.getId(), result.get(0).getId());
        assertEquals(testMaterial.getContent(), result.get(0).getContent());
        assertEquals(testMaterial.getTaskId(), result.get(0).getTaskId());

        verify(materialRepository).findByTaskId(1L);
    }

    @Test
    void getMaterialsByTaskId_ReturnsEmptyList() {
        when(materialRepository.findByTaskId(99L))
            .thenReturn(Collections.emptyList());

        List<Material> result = materialService.getMaterialsByTaskId(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(materialRepository).findByTaskId(99L);
    }

    @Test
    void getMaterialById_ReturnsMaterial() {
        when(materialRepository.findById(1L))
            .thenReturn(Optional.of(testMaterial));

        Material result = materialService.getMaterialById(1L);

        assertNotNull(result);
        assertEquals(testMaterial.getId(), result.getId());
        assertEquals(testMaterial.getContent(), result.getContent());
        assertEquals(testMaterial.getTaskId(), result.getTaskId());

        verify(materialRepository).findById(1L);
    }

    @Test
    void getMaterialById_ReturnsNull() {
        when(materialRepository.findById(99L))
            .thenReturn(Optional.empty());

        Material result = materialService.getMaterialById(99L);

        assertNull(result);

        verify(materialRepository).findById(99L);
    }

    @Test
    void createMaterial_ReturnsSavedMaterial() {
        when(materialRepository.save(any(Material.class)))
            .thenReturn(testMaterial);

        Material result = materialService.createMaterial(testMaterial);

        assertNotNull(result);
        assertEquals(testMaterial.getId(), result.getId());
        assertEquals(testMaterial.getContent(), result.getContent());
        assertEquals(testMaterial.getTaskId(), result.getTaskId());

        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void updateMaterial_ReturnsUpdatedMaterial() {
        when(materialRepository.existsById(1L))
            .thenReturn(true);
        when(materialRepository.save(any(Material.class)))
            .thenReturn(testMaterial);

        Material result = materialService.updateMaterial(1L, testMaterial);

        assertNotNull(result);
        assertEquals(testMaterial.getId(), result.getId());
        assertEquals(testMaterial.getContent(), result.getContent());
        assertEquals(testMaterial.getTaskId(), result.getTaskId());

        verify(materialRepository).existsById(1L);
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void updateMaterial_ReturnsNull() {
        when(materialRepository.existsById(99L))
            .thenReturn(false);

        Material result = materialService.updateMaterial(99L, testMaterial);

        assertNull(result);

        verify(materialRepository).existsById(99L);
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void deleteMaterial_CallsRepository() {
        doNothing().when(materialRepository).deleteById(1L);

        materialService.deleteMaterial(1L);

        verify(materialRepository).deleteById(1L);
    }
}
