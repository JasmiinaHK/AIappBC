package ibu.edu.ba.aiapplication.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    public User() {
        // Obavezan prazan konstruktor za JPA
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 📌 Getteri i Setteri

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
