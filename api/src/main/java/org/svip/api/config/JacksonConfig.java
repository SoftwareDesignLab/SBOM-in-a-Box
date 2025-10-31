package org.svip.api.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // Increase max nesting depth for writing deep JSON structures (e.g., VEX)
        JsonFactory factory = JsonFactory.builder()
                .streamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(5_000)
                        .build())
                .build();
        
        ObjectMapper mapper = new ObjectMapper(factory);
        
        // Register Java 8 date/time module for LocalDateTime support
        mapper.registerModule(new JavaTimeModule());
        
        // Configure to handle circular references globally
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        mapper.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
        
        // Disable writing dates as timestamps (use ISO-8601 strings instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
