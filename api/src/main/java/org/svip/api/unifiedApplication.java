package org.svip.api;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * This class contains the main function which runs both APIs as spring boot applications
 *
 * @author Asa Horn
 */
public class unifiedApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(plugFestApiController.class)
                .web(WebApplicationType.SERVLET) //This is required to prevent "No valid webserver" error.
                .run(args);
        new SpringApplicationBuilder(SVIPApiController.class)
                .web(WebApplicationType.SERVLET) //This is required to prevent "No valid webserver" error.
                .run(args);
    }
}
