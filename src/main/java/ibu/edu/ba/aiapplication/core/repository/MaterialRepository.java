package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {
    Page<Material> findByUserEmail(String userEmail, Pageable pageable);
    
    // Find existing material with same parameters
    Optional<Material> findBySubjectAndGradeAndLessonUnitAndMaterialTypeAndLanguageAndUserEmail(
        String subject, 
        String grade, 
        String lessonUnit, 
        String materialType,
        String language,
        String userEmail
    );
    
    // Find all materials with filtering and sorting
    Page<Material> findAll(Specification<Material> spec, Pageable pageable);
}
