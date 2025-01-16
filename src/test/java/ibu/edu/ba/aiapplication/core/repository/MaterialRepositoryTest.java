package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Material;
import ibu.edu.ba.aiapplication.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialRepositoryTest {

    @Mock
    private MaterialRepository materialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMaterial() {
        // Given
        Material material = new Material();
        material.setSubject("Mathematics");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("test");
        material.setLanguage("english");
        material.setUserEmail("test@example.com");
        
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        // When
        Material savedMaterial = materialRepository.save(material);

        // Then
        assertNotNull(savedMaterial);
        assertEquals("Mathematics", savedMaterial.getSubject());
        assertEquals("5th", savedMaterial.getGrade());
        assertEquals("Fractions", savedMaterial.getLessonUnit());
        assertEquals("test", savedMaterial.getMaterialType());
        assertEquals("english", savedMaterial.getLanguage());
        assertEquals("test@example.com", savedMaterial.getUserEmail());
        verify(materialRepository).save(material);
    }

    @Test
    void testFindById() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        material.setSubject("Mathematics");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("test");
        material.setLanguage("english");
        material.setUserEmail("test@example.com");
        
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        // When
        Optional<Material> found = materialRepository.findById(id);

        // Then
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertEquals("Mathematics", found.get().getSubject());
        assertEquals("5th", found.get().getGrade());
        assertEquals("Fractions", found.get().getLessonUnit());
        assertEquals("test", found.get().getMaterialType());
        assertEquals("english", found.get().getLanguage());
        assertEquals("test@example.com", found.get().getUserEmail());
        verify(materialRepository).findById(id);
    }

    @Test
    void testFindByUserEmail() {
        // Given
        String email = "test@example.com";
        Material material = new Material();
        material.setUserEmail(email);
        material.setSubject("Mathematics");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("test");
        material.setLanguage("english");
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        when(materialRepository.findByUserEmail(email, pageable))
            .thenReturn(new PageImpl<>(Arrays.asList(material), pageable, 1));

        // When
        Page<Material> materials = materialRepository.findByUserEmail(email, pageable);

        // Then
        assertNotNull(materials);
        assertFalse(materials.isEmpty());
        assertEquals(email, materials.getContent().get(0).getUserEmail());
        assertEquals("Mathematics", materials.getContent().get(0).getSubject());
        assertEquals("5th", materials.getContent().get(0).getGrade());
        assertEquals("Fractions", materials.getContent().get(0).getLessonUnit());
        assertEquals("test", materials.getContent().get(0).getMaterialType());
        assertEquals("english", materials.getContent().get(0).getLanguage());
        verify(materialRepository).findByUserEmail(email, pageable);
    }

    @Test
    void testFindByUserEmail_NoMaterials() {
        // Given
        String email = "nonexistent@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        
        when(materialRepository.findByUserEmail(email, pageable))
            .thenReturn(new PageImpl<>(Arrays.asList(), pageable, 0));

        // When
        Page<Material> materials = materialRepository.findByUserEmail(email, pageable);

        // Then
        assertNotNull(materials);
        assertTrue(materials.isEmpty());
        verify(materialRepository).findByUserEmail(email, pageable);
    }

    @Test
    void testDeleteMaterial() {
        // Given
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        // When
        materialRepository.deleteById(id);

        // Then
        verify(materialRepository).deleteById(id);
    }

    @Test
    void findByUserEmail_Success() {
        // Given
        String email = "test@example.com";
        Material material = new Material();
        material.setUserEmail(email);
        material.setSubject("Mathematics");
        material.setGrade("5th");
        material.setLessonUnit("Fractions");
        material.setMaterialType("test");
        material.setLanguage("english");
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        when(materialRepository.findByUserEmail(email, pageable))
            .thenReturn(new PageImpl<>(Arrays.asList(material), pageable, 1));

        // When
        Page<Material> materials = materialRepository.findByUserEmail(email, pageable);

        // Then
        assertNotNull(materials);
        assertFalse(materials.isEmpty());
        assertEquals(email, materials.getContent().get(0).getUserEmail());
        assertEquals("Mathematics", materials.getContent().get(0).getSubject());
        assertEquals("5th", materials.getContent().get(0).getGrade());
        assertEquals("Fractions", materials.getContent().get(0).getLessonUnit());
        assertEquals("test", materials.getContent().get(0).getMaterialType());
        assertEquals("english", materials.getContent().get(0).getLanguage());
        verify(materialRepository).findByUserEmail(email, pageable);
    }

    @Test
    void findByUserEmail_NoMaterials() {
        // Given
        String email = "nonexistent@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        
        when(materialRepository.findByUserEmail(email, pageable))
            .thenReturn(new PageImpl<>(Arrays.asList(), pageable, 0));

        // When
        Page<Material> materials = materialRepository.findByUserEmail(email, pageable);

        // Then
        assertNotNull(materials);
        assertTrue(materials.isEmpty());
        verify(materialRepository).findByUserEmail(email, pageable);
    }

    @Test
    void findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail_Success() {
        // Given
        String subject = "Mathematics";
        String grade = "5th";
        String lessonUnit = "Fractions";
        String materialType = "test";
        String language = "english";
        String email = "test@example.com";

        Material material = new Material();
        material.setUserEmail(email);
        material.setSubject(subject);
        material.setGrade(grade);
        material.setLessonUnit(lessonUnit);
        material.setMaterialType(materialType);
        material.setLanguage(language);

        when(materialRepository.findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email))
            .thenReturn(Optional.of(material));

        // When
        Optional<Material> result = materialRepository.findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(subject, result.get().getSubject());
        assertEquals(grade, result.get().getGrade());
        assertEquals(lessonUnit, result.get().getLessonUnit());
        assertEquals(materialType, result.get().getMaterialType());
        assertEquals(language, result.get().getLanguage());
        assertEquals(email, result.get().getUserEmail());
        verify(materialRepository).findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email);
    }

    @Test
    void findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail_NotFound() {
        // Given
        String subject = "NonexistentSubject";
        String grade = "NonexistentGrade";
        String lessonUnit = "NonexistentUnit";
        String materialType = "NonexistentType";
        String language = "NonexistentLanguage";
        String email = "nonexistent@example.com";

        when(materialRepository.findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email))
            .thenReturn(Optional.empty());

        // When
        Optional<Material> result = materialRepository.findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email);

        // Then
        assertFalse(result.isPresent());
        verify(materialRepository).findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
                subject, grade, lessonUnit, materialType, language, email);
    }
}
