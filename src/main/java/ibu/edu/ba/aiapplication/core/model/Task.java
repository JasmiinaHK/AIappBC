package ibu.edu.ba.aiapplication.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false)
    private String grade;
    
    @Column(nullable = false)
    private String lessonUnit;
    
    @Column(nullable = false)
    private String materialType;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Material> materials = new HashSet<>();
    
    @Column
    private String language;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Task() {
    }

    public Task(String id, String subject, String grade, String lessonUnit, String materialType, 
                String content, String userEmail, User user) {
        this.id = id;
        this.subject = subject;
        this.grade = grade;
        this.lessonUnit = lessonUnit;
        this.materialType = materialType;
        this.content = content;
        this.userEmail = userEmail;
        this.user = user;
    }

    public void addMaterial(Material material) {
        materials.add(material);
        material.setTask(this);
    }

    public void removeMaterial(Material material) {
        materials.remove(material);
        material.setTask(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
