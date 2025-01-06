package ibu.edu.ba.aiapplication.repository;

import ibu.edu.ba.aiapplication.core.model.User;
import ibu.edu.ba.aiapplication.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // given
        User user = new User("test@ibu.ba", "Test User");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@ibu.ba");
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        User user = new User("test@ibu.ba", "Test User");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@ibu.ba");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotFindUserByNonExistentEmail() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@ibu.ba");

        // then
        assertThat(exists).isFalse();
    }
}
