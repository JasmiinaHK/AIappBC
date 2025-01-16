package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 📌 Dohvati korisnika po emailu
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 📌 Dohvati ime korisnika po emailu
    public String getUserNameByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return user.getName();
    }
}
