package com.DYR.proyecto.auth.controller;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private static boolean isAdmin(Usuario u) {
        return u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN");
    }

    @GetMapping
    public String list(Model model) {
        var usuarios = usuarioService.listarUsuarios().stream()
                .filter(u -> !isAdmin(u))
                .collect(Collectors.toList());
        model.addAttribute("usuarios", usuarios);
        return "formularios/usuarios/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        var usuario = usuarioService.buscarUsuario(id);
        if (isAdmin(usuario)) return "redirect:/usuarios";
        model.addAttribute("usuario", usuario);
        return "formularios/usuarios/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var usuario = usuarioService.buscarUsuario(id);
        if (isAdmin(usuario)) return "redirect:/usuarios";
        model.addAttribute("usuario", usuario);
        return "formularios/usuarios/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String role) {
        var usuario = usuarioService.buscarUsuario(id);
        if (isAdmin(usuario)) return "redirect:/usuarios";
        usuario.setName(name);
        usuario.setEmail(email);
        usuario.setPhone(phone);
        if (role != null && !role.isBlank()) usuario.setRole(role);
        usuarioService.actualizarUsuario(id, usuario);
        return "redirect:/usuarios/" + id + "?success=Usuario actualizado";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        var usuario = usuarioService.buscarUsuario(id);
        if (isAdmin(usuario)) return "redirect:/usuarios";
        usuario.setEstado("inactivo");
        usuarioService.actualizarUsuario(id, usuario);
        return "redirect:/usuarios?success=Usuario inhabilitado";
    }

    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        var usuario = usuarioService.buscarUsuario(id);
        if (isAdmin(usuario)) return "redirect:/usuarios";
        usuario.setEstado("activo");
        usuarioService.actualizarUsuario(id, usuario);
        return "redirect:/usuarios?success=Usuario habilitado";
    }
}