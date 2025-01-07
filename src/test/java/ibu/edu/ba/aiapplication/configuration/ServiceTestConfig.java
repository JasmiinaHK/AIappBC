package ibu.edu.ba.aiapplication.configuration;

import ibu.edu.ba.aiapplication.core.repository.MaterialRepository;
import ibu.edu.ba.aiapplication.core.service.MaterialService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ServiceTestConfig {
    
    @Bean
    @Primary
    public MaterialRepository materialRepository() {
        return Mockito.mock(MaterialRepository.class);
    }

    @Bean
    @Primary
    public MaterialService materialService(MaterialRepository materialRepository) {
        return new MaterialService(materialRepository);
    }
}
