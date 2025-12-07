package com.DYR.proyecto.auth.config;

import com.DYR.proyecto.auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEstadoInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public UsuarioEstadoInitializer(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Actualizar todos los usuarios que tengan estado NULL o vacío a 'activo'
        var usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> {
            if (u.getEstado() == null || u.getEstado().isBlank()) {
                u.setEstado("activo");
                usuarioRepository.save(u);
            }
        });
    }
}
