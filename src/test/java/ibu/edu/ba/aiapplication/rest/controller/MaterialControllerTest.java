package ibu.edu.ba.aiapplication.rest.controller;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialControllerTest {

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private MaterialController materialController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMaterial_Success() {
        // Given
        Material material = new Material();
        material.setUserEmail("test@test.com");
        material.setSubject("Math");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("Worksheet");

        when(materialService.createMaterial(any(Material.class))).thenReturn(material);

        // When
        ResponseEntity<?> response = materialController.createMaterial(material);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Material);
        Material result = (Material) response.getBody();
        assertEquals("test@test.com", result.getUserEmail());
        assertEquals("Math", result.getSubject());
        verify(materialService).createMaterial(material);
    }

    @Test
    void createMaterial_Error() {
        // Given
        Material material = new Material();
        when(materialService.createMaterial(any(Material.class)))
                .thenThrow(new RuntimeException("Test error"));

        // When
        ResponseEntity<?> response = materialController.createMaterial(material);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertTrue(error.containsKey("error"));
        assertTrue(error.get("error").contains("Test error"));
        verify(materialService).createMaterial(material);
    }

    @Test
    void generateContent_Success() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setUserEmail("test@test.com");
        material.setGeneratedContent("Generated content");

        when(materialService.generateContent(eq(id), eq("test@test.com"), any(Material.class)))
                .thenReturn(material);

        // When
        ResponseEntity<?> response = materialController.generateContent(id, material);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Material);
        Material result = (Material) response.getBody();
        assertEquals("Generated content", result.getGeneratedContent());
        verify(materialService).generateContent(eq(id), eq("test@test.com"), any(Material.class));
    }

    @Test
    void generateContent_Error() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setUserEmail("test@test.com");
        when(materialService.generateContent(eq(id), anyString(), any(Material.class)))
                .thenThrow(new IllegalArgumentException("Generation error"));

        // When
        ResponseEntity<?> response = materialController.generateContent(id, material);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertTrue(error.containsKey("error"));
        assertEquals("Generation error", error.get("error"));
    }

    @Test
    void getMaterialsByUserEmail_Success() {
        // Given
        String email = "test@test.com";
        Material material = new Material();
        material.setUserEmail(email);
        Page<Material> page = new PageImpl<>(Collections.singletonList(material));

        when(materialService.getMaterialsByUserEmail(eq(email), anyInt(), anyInt())).thenReturn(page);

        // When
        ResponseEntity<?> response = materialController.getMaterialsByUserEmail(email, 0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Page);
        @SuppressWarnings("unchecked")
        Page<Material> result = (Page<Material>) response.getBody();
        assertEquals(1, result.getTotalElements());
        assertEquals(email, result.getContent().get(0).getUserEmail());
        verify(materialService).getMaterialsByUserEmail(email, 0, 10);
    }

    @Test
    void getMaterialsByUserEmail_Error() {
        // Given
        String email = "test@test.com";
        when(materialService.getMaterialsByUserEmail(eq(email), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Test error"));

        // When
        ResponseEntity<?> response = materialController.getMaterialsByUserEmail(email, 0, 10);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertTrue(error.containsKey("error"));
        assertTrue(error.get("error").contains("Test error"));
    }

    @Test
    void getMaterialById_Success() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        material.setUserEmail("test@test.com");

        when(materialService.getMaterialById(id)).thenReturn(Optional.of(material));

        // When
        ResponseEntity<?> response = materialController.getMaterialById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Material);
        Material result = (Material) response.getBody();
        assertEquals(id, result.getId());
        verify(materialService).getMaterialById(id);
    }

    @Test
    void getMaterialById_NotFound() {
        // Given
        Long id = 1L;
        when(materialService.getMaterialById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = materialController.getMaterialById(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(materialService).getMaterialById(id);
    }

    @Test
    void deleteMaterial_Success() {
        // Given
        Long id = 1L;
        String userEmail = "test@test.com";
        doNothing().when(materialService).deleteMaterial(id, userEmail);

        // When
        ResponseEntity<?> response = materialController.deleteMaterial(id, userEmail);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(materialService).deleteMaterial(id, userEmail);
    }

    @Test
    void deleteMaterial_Error() {
        // Given
        Long id = 1L;
        String userEmail = "test@test.com";
        doThrow(new RuntimeException("Delete error")).when(materialService).deleteMaterial(id, userEmail);

        // When
        ResponseEntity<?> response = materialController.deleteMaterial(id, userEmail);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertTrue(error.containsKey("error"));
        assertTrue(error.get("error").contains("Delete error"));
    }
}
