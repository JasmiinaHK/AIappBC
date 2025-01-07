package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserEmail(String userEmail);
    List<Task> findByUserEmailAndSubject(String userEmail, String subject);
    List<Task> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Task> findByUserEmailAndSubjectOrderByCreatedAtDesc(String userEmail, String subject);
}
