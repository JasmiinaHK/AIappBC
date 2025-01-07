package ibu.edu.ba.aiapplication.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_task_user_email", columnList = "user_email"),
        @Index(name = "idx_task_subject", columnList = "subject"),
        @Index(name = "idx_task_created", columnList = "created_at")
})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User email is required")
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @NotBlank(message = "Subject is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "Grade is required")
    @Size(min = 1, max = 20)
    @Column(nullable = false)
    private String grade;

    @NotBlank(message = "Lesson unit is required")
    @Size(min = 2, max = 100)
    @Column(name = "lesson_unit", nullable = false)
    private String lessonUnit;

    @NotBlank(message = "Material type is required")
    @Size(min = 2, max = 50)
    @Column(name = "material_type", nullable = false)
    private String materialType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Size(max = 10)
    @Column
    private String language;

    @Size(min = 2, max = 50)
    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Task() {
    }

    public Task(String userEmail, String subject, String grade, String lessonUnit, String materialType, String language, String name) {
        this.userEmail = userEmail;
        this.subject = subject;
        this.grade = grade;
        this.lessonUnit = lessonUnit;
        this.materialType = materialType;
        this.language = language;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLessonUnit() {
        return lessonUnit;
    }

    public void setLessonUnit(String lessonUnit) {
        this.lessonUnit = lessonUnit;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
