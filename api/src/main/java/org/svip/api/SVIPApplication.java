package org.svip.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * This class contains the main function which runs both APIs as spring boot applications
 *
 * @author Asa Horn
 */
@SpringBootApplication
public class SVIPApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SVIPApplication.class, args);
    }
}
