package com.DYR.proyecto.config;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean adminExists = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN"));

        if (!adminExists) {
            // Credenciales por defecto (cámbialas en producción)
            String adminEmail = "admin@dyr.com";
            String adminPassword = "Admin123!";

            Usuario admin = new Usuario();
            admin.setUsername(adminEmail);
            admin.setEmail(adminEmail);
            admin.setName("Administrador");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");

            usuarioRepository.save(admin);
            log.info("Admin creado automáticamente: {} / (contraseña por defecto)", adminEmail);
        } else {
            log.info("Ya existe al menos un usuario ADMIN; no se creó usuario por defecto.");
        }
    }
}
