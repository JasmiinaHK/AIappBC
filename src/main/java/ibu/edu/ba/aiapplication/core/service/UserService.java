package ibu.edu.ba.aiapplication.core.service;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }
        return userRepository.save(user);
    }

    public User updateUser(String email, User updatedUser) {
        User user = getUserByEmail(email);
        user.setName(updatedUser.getName());
        return userRepository.save(user);
    }

    public void deleteUser(String email) {
        User user = getUserByEmail(email);
        userRepository.delete(user);
    }
}
