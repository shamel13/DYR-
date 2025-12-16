package com.DYR.proyecto.auth.service;

import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario existente = buscarUsuario(id);
        existente.setUsername(usuario.getUsername());
        existente.setName(usuario.getName());
        existente.setEmail(usuario.getEmail());
        existente.setPassword(usuario.getPassword());
        existente.setRole(usuario.getRole());
        return usuarioRepository.save(existente);
    }

    public void actualizarUsuario(Long id, String name, String email, String phone, String address, String city) {
        Usuario existente = buscarUsuario(id);
        existente.setName(name);
        existente.setEmail(email);
        existente.setPhone(phone);
        existente.setAddress(address);
        existente.setCity(city);
        usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void desactivarUsuario(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setEstado("inactivo");
        usuarioRepository.save(usuario);
    }

    public void activarUsuario(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setEstado("activo");
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

}
