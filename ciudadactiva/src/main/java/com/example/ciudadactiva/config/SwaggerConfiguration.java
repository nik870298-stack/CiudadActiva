// SwaggerConfiguration.java
package com.example.ciudadactiva.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CiudadActiva API Rest").version("1.0.0").description("Backend completo para gestión e incidencias ciudadanas"))
                .addSecurityItem(new SecurityRequirement().addList("JavaAuth"))
                .components(new Components().addSecuritySchemes("JavaAuth",
                        new SecurityScheme().name("JavaAuth").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}