package ibu.edu.ba.aiapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "ibu.edu.ba.aiapplication")
@EntityScan("ibu.edu.ba.aiapplication.core.model")
@EnableJpaRepositories("ibu.edu.ba.aiapplication.core.repository")
@RestController
public class AIApplication {

    @GetMapping("/test-app")
    public String testApp() {
        return "Application is running!";
    }

    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
    }
}
