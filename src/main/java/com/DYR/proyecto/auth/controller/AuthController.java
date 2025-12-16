package com.DYR.proyecto.auth.controller;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.repository.UsuarioRepository;
import com.DYR.proyecto.auth.security.CustomUserDetailsService;
import com.DYR.proyecto.auth.service.JWTService;
import com.DYR.proyecto.ventas.repository.VentaRepository;
import com.DYR.proyecto.ventas.model.Venta;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private com.DYR.proyecto.pedido.repository.PedidoRepository pedidoRepository;

    // -------------------
    // 🔹 VISTAS MVC
    // -------------------

    // Vista de login
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("error", false);
        return "pagina/login"; // Renderiza login.html
    }
    
    // POST manual para login (en caso de que formLogin no funcione)
    @PostMapping("/login")
    public String loginSubmit(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            Model model,
            jakarta.servlet.http.HttpSession session,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {
        try {
            if (username != null && password != null) {
                System.out.println("DEBUG: Intento login con username=" + username);
                Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("DEBUG: Autenticación exitosa");
                
                // ✅ GUARDAR SecurityContext EN LA SESIÓN para que persista en el redirect
                HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
                repo.saveContext(SecurityContextHolder.getContext(), request, response);
                System.out.println("DEBUG: SecurityContext guardado en sesión");
                
                // cargar usuario y guardarlo en sesión para mostrar perfil
                var isAdmin = new boolean[]{false}; // usar array para modificar dentro del lambda
                usuarioRepository.findByUsername(username).ifPresent(u -> {
                    String displayName = (u.getName() != null && !u.getName().isBlank()) ? u.getName() : u.getEmail();
                    session.setAttribute("currentUserName", displayName);
                    session.setAttribute("currentUserEmail", u.getEmail());
                    session.setAttribute("currentUserId", u.getId());
                    System.out.println("DEBUG: Usuario guardado en sesión: " + displayName + ", Role: " + u.getRole());
                    // Verificar si es ADMIN
                    if (u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN")) {
                        isAdmin[0] = true;
                    }
                });
                // Redirigir según rol
                if (isAdmin[0]) {
                    System.out.println("DEBUG: Redirigiendo a dashboard (ADMIN)");
                    return "redirect:/dashboard/home";
                }
                System.out.println("DEBUG: Redirigiendo a index (USER)");
                return "redirect:/pagina/index";
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error en login: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/auth/login?error";
        }
        return "redirect:/auth/login?error";
    }

    // Vista de registro (Thymeleaf)
    @GetMapping("/register")
    public String registerForm() {
        return "pagina/register";
    }

    // Procesar registro MVC
    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model) {
        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("usuario", usuario);
            return "pagina/register";
        }
        
        // Verificar si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            model.addAttribute("usuario", usuario);
            return "pagina/register";
        }
        
        // Verificar si el documento ya está registrado
        if (usuarioRepository.findByDocumentNumber(usuario.getDocumentNumber()).isPresent()) {
            model.addAttribute("error", "El número de documento ya está registrado");
            model.addAttribute("usuario", usuario);
            return "pagina/register";
        }
        
        // Si no se proporcionó 'username' en el formulario, usar el email como username
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            usuario.setUsername(usuario.getEmail());
        }
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

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        try {
            session.invalidate();
        } catch (Exception ignored) {}
        return "redirect:/pagina/index?logout";
    }

    // Mostrar perfil y formulario de edición
    @GetMapping("/profile")
    public String profileForm(Model model, jakarta.servlet.http.HttpSession session) {
        Object idObj = session.getAttribute("currentUserId");
        if (idObj == null) return "redirect:/auth/login";
        Long id = (idObj instanceof Long) ? (Long) idObj : Long.parseLong(idObj.toString());
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u == null) return "redirect:/auth/login";
        
        // Obtener historial de compras del usuario
        List<Venta> compras = ventaRepository.findByUserOrderByFechaVentaDesc(u);
        
        // Calcular estadísticas
        double totalGastado = compras.stream().mapToDouble(Venta::getTotal).sum();
        int totalProductos = compras.stream().mapToInt(Venta::getCantidad).sum();
        
        // Obtener pedidos del usuario
        var pedidos = pedidoRepository.findByUserOrderByFechaRegistroDesc(u);
        System.out.println("DEBUG: Pedidos encontrados para usuario " + u.getUsername() + ": " + pedidos.size());
        
        model.addAttribute("usuario", u);
        model.addAttribute("compras", compras);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalGastado", totalGastado);
        model.addAttribute("totalProductos", totalProductos);
        // asegurar nombre en modelo para header
        String displayName = (u.getName() != null && !u.getName().isBlank()) ? u.getName() : u.getEmail();
        model.addAttribute("currentUserName", displayName);
        return "pagina/profile";
    }

    // Procesar edición de perfil
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Usuario usuarioForm, jakarta.servlet.http.HttpSession session) {
        Object idObj = session.getAttribute("currentUserId");
        if (idObj == null) return "redirect:/auth/login";
        Long id = (idObj instanceof Long) ? (Long) idObj : Long.parseLong(idObj.toString());
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u == null) return "redirect:/auth/login";

        u.setName(usuarioForm.getName());
        u.setEmail(usuarioForm.getEmail());
        u.setDocumentType(usuarioForm.getDocumentType());
        u.setDocumentNumber(usuarioForm.getDocumentNumber());
        u.setPhone(usuarioForm.getPhone());
        u.setAddress(usuarioForm.getAddress());
        u.setNeighborhood(usuarioForm.getNeighborhood());
        u.setCity(usuarioForm.getCity());
        u.setPostalCode(usuarioForm.getPostalCode());
        u.setCountry(usuarioForm.getCountry());
        // Si se proporciona nueva contraseña, cambiarla
        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
        }
        // Mantener username = email (política actual)
        if (u.getEmail() != null) u.setUsername(u.getEmail());

        usuarioRepository.save(u);

        String displayName = (u.getName() != null && !u.getName().isBlank()) ? u.getName() : u.getEmail();
        session.setAttribute("currentUserName", displayName);
        session.setAttribute("currentUserEmail", u.getEmail());

        return "redirect:/pagina/index";
    }

    // -------------------
    // 🔹 ENDPOINTS REST (JWT)
    // -------------------

    // Login API → devuelve token
    @PostMapping("/login-api")
    @ResponseBody
    public Map<String, String> loginApi(@RequestParam String username, @RequestParam String password) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        return Map.of("token", token);
    }

    // Registro API → crea usuario y devuelve token
    @PostMapping("/register-api")
    @ResponseBody
    public Map<String, String> registerApi(@Valid @RequestBody Usuario usuario, BindingResult result) {
        // Crear un mapa de respuesta
        Map<String, String> response = new HashMap<>();
        
        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            result.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("; "));
            response.put("error", errors.toString());
            return response;
        }
        
        // Verificar si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            response.put("error", "El email ya está registrado");
            return response;
        }
        
        // Verificar si el documento ya está registrado
        if (usuarioRepository.findByDocumentNumber(usuario.getDocumentNumber()).isPresent()) {
            response.put("error", "El número de documento ya está registrado");
            return response;
        }
        
        // Asegurar username: si viene vacío, usar el email
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            usuario.setUsername(usuario.getEmail());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("USER");
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String token = jwtService.generateToken(userDetails);

        response.put("token", token);
        return response;
    }
}
