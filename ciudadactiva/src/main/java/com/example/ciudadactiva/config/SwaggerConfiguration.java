package com.example.ciudadactiva.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API Usurio")
                        .version("1.0")
                        .description("Documentacion de la API para gestionar BD en la tabla usuarios")
                        .contact(new Contact()
                                .name("Soporte API")
                                .email("nalbertorodriguez@ucundinamarca.edu.co")));
    }
}
