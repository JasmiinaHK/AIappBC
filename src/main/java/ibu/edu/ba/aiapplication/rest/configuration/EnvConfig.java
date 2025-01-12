package ibu.edu.ba.aiapplication.rest.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@PropertySource("file:.env")
public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    @Autowired
    private Environment env;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        logger.info("Loading environment variables...");
        String openaiKey = env.getProperty("OPENAI_API_KEY");
        logger.info("OpenAI API Key loaded: {}", 
            openaiKey != null ? openaiKey.substring(0, Math.min(10, openaiKey.length())) + "..." : "null");
    }
}
