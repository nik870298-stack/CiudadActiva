package com.example.ciudadactiva.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Públicos / Swagger
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/health").permitAll()

                        // Categorías
                        .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/categorias").authenticated()

                        // Estadísticas (Exclusivo ADMINISTRADOR)
                        .requestMatchers("/api/estadisticas/**").hasRole("ADMINISTRADOR")

                        // Reportes
                        .requestMatchers(HttpMethod.POST, "/api/reportes").hasRole("CIUDADANO")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/mis-reportes").hasRole("CIUDADANO")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/{id}/seguimientos").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/reportes/{id}/validar").hasAnyRole("GESTOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reportes/{id}/asignaciones").hasAnyRole("GESTOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/reportes/{id}/cerrar").hasRole("OPERADOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}