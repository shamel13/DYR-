package com.DYR.proyecto.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .securityContext(context -> context
                .securityContextRepository(new HttpSessionSecurityContextRepository())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(authz -> authz
                // Rutas públicas primero (alto nivel de precedencia)
                .requestMatchers("/").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/pagina/**").permitAll()
                .requestMatchers("/pagos/nuevo").permitAll()
                .requestMatchers("/pagos").permitAll()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/uploads/**").permitAll()
                // Solo administradores pueden acceder al dashboard, formularios y gestión
                .requestMatchers("/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/pedidos/**").hasRole("ADMIN")
                .requestMatchers("/formularios/**").hasRole("ADMIN")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            // Desactivar formLogin para que el AuthController maneje el login manualmente
            // .formLogin(form -> form.loginPage("/auth/login").permitAll())
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/pagina/index")
            );

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
