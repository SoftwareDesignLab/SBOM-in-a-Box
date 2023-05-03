package org.svip.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * This class contains the main function which runs both APIs as spring boot applications
 *
 * @author Asa Horn
 */
@SpringBootApplication
public class UnifiedApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(UnifiedApplication.class, args);
    }
}
