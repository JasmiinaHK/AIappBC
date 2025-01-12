package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import ibu.edu.ba.aiapplication.rest.configuration.OpenAIConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private OpenAIConfiguration openAIConfiguration;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void createMaterial_Success() {
        // Given
        Material material = new Material();
        material.setUserEmail("test@test.com");
        material.setSubject("Math");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("Worksheet");

        when(materialRepository.save(any(Material.class))).thenReturn(material);

        // When
        Material result = materialService.createMaterial(material);

        // Then
        assertNotNull(result);
        assertEquals("test@test.com", result.getUserEmail());
        assertEquals("Math", result.getSubject());
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void getMaterialsByUserEmail_Success() {
        // Given
        String userEmail = "test@test.com";
        Material material = new Material();
        material.setUserEmail(userEmail);
        Page<Material> page = new PageImpl<>(Collections.singletonList(material));
        when(materialRepository.findByUserEmail(eq(userEmail), any(Pageable.class))).thenReturn(page);

        // When
        Page<Material> result = materialService.getMaterialsByUserEmail(userEmail, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userEmail, result.getContent().get(0).getUserEmail());
        verify(materialRepository).findByUserEmail(eq(userEmail), any(Pageable.class));
    }

    @Test
    void getMaterialById_Success() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        material.setUserEmail("test@test.com");
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        // When
        Optional<Material> result = materialService.getMaterialById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(materialRepository).findById(id);
    }

    @Test
    void getMaterialById_NotFound() {
        // Given
        Long id = 1L;
        when(materialRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Material> result = materialService.getMaterialById(id);

        // Then
        assertFalse(result.isPresent());
        verify(materialRepository).findById(id);
    }

    @Test
    void deleteMaterial_Success() {
        // Given
        Long id = 1L;
        String userEmail = "test@test.com";
        Material material = new Material();
        material.setId(id);
        material.setUserEmail(userEmail);
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));
        doNothing().when(materialRepository).deleteById(id);

        // When
        materialService.deleteMaterial(id, userEmail);

        // Then
        verify(materialRepository).findById(id);
        verify(materialRepository).deleteById(id);
    }

    @Test
    void deleteMaterial_NotFound() {
        // Given
        Long id = 1L;
        String userEmail = "test@test.com";
        when(materialRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> materialService.deleteMaterial(id, userEmail));
        verify(materialRepository).findById(id);
        verify(materialRepository, never()).deleteById(any());
    }

    @Test
    void deleteMaterial_UnauthorizedUser() {
        // Given
        Long id = 1L;
        String userEmail = "test@test.com";
        Material material = new Material();
        material.setId(id);
        material.setUserEmail("other@test.com");
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        // When & Then
        assertThrows(RuntimeException.class, () -> materialService.deleteMaterial(id, userEmail));
        verify(materialRepository).findById(id);
        verify(materialRepository, never()).deleteById(any());
    }
}
