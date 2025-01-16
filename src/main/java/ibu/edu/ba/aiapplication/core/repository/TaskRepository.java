package ibu.edu.ba.aiapplication.core.repository;

import ibu.edu.ba.aiapplication.core.model.Task;
import ibu.edu.ba.aiapplication.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 📌 Dohvati sve zadatke po emailu korisnika
    List<Task> findByUserEmail(String userEmail);

    // 📌 Dohvati sve zadatke po korisniku
    List<Task> findByUser(User user);

    // 📌 Dohvati sve zadatke po ID-u korisnika
    List<Task> findByUserId(Long userId);

    // 📌 Dohvati zadatke na osnovu predmeta
    List<Task> findBySubject(String subject);

    // 📌 Dohvati zadatke na osnovu razreda
    List<Task> findByGrade(String grade);

    // 📌 Dohvati zadatke na osnovu nastavne jedinice
    List<Task> findByLessonUnit(String lessonUnit);

    // 📌 Dohvati zadatke na osnovu tipa materijala
    List<Task> findByMaterialType(String materialType);

    // 📌 Dohvati zadatke na osnovu generisanog sadržaja
    List<Task> findByGeneratedContentContaining(String content);
}
