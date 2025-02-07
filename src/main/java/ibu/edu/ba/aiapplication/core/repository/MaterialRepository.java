package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByTaskId(Long taskId);
}
