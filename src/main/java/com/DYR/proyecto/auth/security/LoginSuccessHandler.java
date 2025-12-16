package com.DYR.proyecto.auth.security;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);

    public LoginSuccessHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            HttpSession session = request.getSession(true);
            String displayName = (u.getName() != null && !u.getName().isBlank()) ? u.getName() : u.getEmail();
            session.setAttribute("currentUserName", displayName);
            session.setAttribute("currentUserEmail", u.getEmail());
            session.setAttribute("currentUserId", u.getId());
            log.info("LoginSuccessHandler: sesión poblada para usuario={}", username);
        });

        response.sendRedirect(request.getContextPath() + "/pagina/index");
    }
}
