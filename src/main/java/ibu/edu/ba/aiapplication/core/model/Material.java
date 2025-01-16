package ibu.edu.ba.aiapplication.core.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;      // Predmet
    private String grade;        // Razred
    private String lessonUnit;   // Nastavna jedinica
    private String materialType; // Tip materijala
    private String language;     // Jezik
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String generatedContent;  // Generisani sadržaj
    
    private String userEmail;    // Email korisnika

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Nested SearchOperation enum
    public enum SearchOperation {
        EQUALS,
        LIKE,
        GREATER_THAN,
        LESS_THAN
    }

    // Nested SearchCriteria class
    public static class SearchCriteria {
        private String key;
        private SearchOperation operation;
        private Object value;

        public SearchCriteria(String key, SearchOperation operation, Object value) {
            this.key = key;
            this.operation = operation;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public SearchOperation getOperation() {
            return operation;
        }

        public void setOperation(SearchOperation operation) {
            this.operation = operation;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    // Nested MaterialSpecification class
    public static class MaterialSpecification implements Specification<Material> {
        private final List<SearchCriteria> criteria;

        public MaterialSpecification() {
            this.criteria = new ArrayList<>();
        }

        public void add(SearchCriteria criteria) {
            this.criteria.add(criteria);
        }

        @Override
        public jakarta.persistence.criteria.Predicate toPredicate(
            jakarta.persistence.criteria.Root<Material> root, 
            jakarta.persistence.criteria.CriteriaQuery<?> query, 
            jakarta.persistence.criteria.CriteriaBuilder builder) {
            
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            for (SearchCriteria criteria : this.criteria) {
                switch (criteria.getOperation()) {
                    case EQUALS:
                        predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                        break;
                    case LIKE:
                        predicates.add(builder.like(builder.lower(root.get(criteria.getKey())), 
                            "%" + criteria.getValue().toString().toLowerCase() + "%"));
                        break;
                    case GREATER_THAN:
                        if (root.get(criteria.getKey()).getJavaType() == LocalDateTime.class) {
                            predicates.add(builder.greaterThan(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                        }
                        break;
                    case LESS_THAN:
                        if (root.get(criteria.getKey()).getJavaType() == LocalDateTime.class) {
                            predicates.add(builder.lessThan(root.get(criteria.getKey()), (LocalDateTime) criteria.getValue()));
                        }
                        break;
                }
            }

            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }
    }

    // Constructors
    public Material() {
    }

    public Material(String subject, String grade, String lessonUnit, String materialType, String language, String generatedContent, String userEmail) {
        this.subject = subject;
        this.grade = grade;
        this.lessonUnit = lessonUnit;
        this.materialType = materialType;
        this.language = language;
        this.generatedContent = generatedContent;
        this.userEmail = userEmail;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(String generatedContent) {
        this.generatedContent = generatedContent;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return generatedContent; // If you want the description to be the same as the generated content.
    }

    public void setDescription(String description) {
        this.generatedContent = description; // Setting the description if you want to keep them equal
    }
}
