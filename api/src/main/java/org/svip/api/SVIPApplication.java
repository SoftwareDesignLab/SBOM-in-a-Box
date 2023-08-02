package org.svip.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

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
