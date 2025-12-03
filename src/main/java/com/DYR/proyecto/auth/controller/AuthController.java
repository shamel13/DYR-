package com.DYR.proyecto.auth.controller;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.repository.UsuarioRepository;
import com.DYR.proyecto.auth.security.CustomUserDetailsService;
import com.DYR.proyecto.auth.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // -------------------
    // 🔹 VISTAS MVC
    // -------------------

    // Vista de login
    @GetMapping("/login")
    public String login() {
        return "pagina/login"; // Renderiza login.html
    }

    // Vista de registro
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "pagina/register"; // Renderiza register.html
    }

    // Procesar registro MVC
    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("USER");
        usuarioRepository.save(usuario);
        return "redirect:/auth/login?registered";
    }

    // Logout MVC
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/pagina/index";
    }

    // -------------------
    // 🔹 ENDPOINTS REST (JWT)
    // -------------------

    // Login API → devuelve token
    @PostMapping("/login-api")
    @ResponseBody
    public Map<String, String> loginApi(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        return Map.of("token", token);
    }

    // Registro API → crea usuario y devuelve token
    @PostMapping("/register-api")
    @ResponseBody
    public Map<String, String> registerApi(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("USER");
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String token = jwtService.generateToken(userDetails);

        return Map.of("token", token);
    }
}
