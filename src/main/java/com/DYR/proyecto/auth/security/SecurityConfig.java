package com.DYR.proyecto.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())

            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/pagina/**", "/auth/**").permitAll()

                // MVC protegidos por rol
                .requestMatchers("/clientes/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/productos/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/pedidos/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/ventas/**").hasRole("ADMIN")

                // Dashboard requiere autenticación
                .requestMatchers("/dashboard/**").authenticated()

                // API REST → requieren autenticación (JWT normalmente)
                .requestMatchers("/api/**").authenticated()

                // Todo lo demás
                .anyRequest().authenticated()
            )

            // Login basado en formulario (para MVC/Thymeleaf)
            .formLogin(form -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/dashboard/home", true)
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/pagina/index")
                .permitAll()
            )

            // Configuración de sesión: STATELESS para API REST
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
