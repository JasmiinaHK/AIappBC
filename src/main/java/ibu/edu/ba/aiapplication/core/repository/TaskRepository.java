package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserEmail(String email);
}
