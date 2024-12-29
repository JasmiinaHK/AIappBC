package ibu.edu.ba.aiapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:.env")
public class EnvConfig {
}
