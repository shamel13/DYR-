package com.DYR.proyecto.pagina.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagina")
public class PaginaController {

    @GetMapping("/index")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(Map.of("message", "Página principal funcionando"));
    }

    @GetMapping("/catalogo")
    public ResponseEntity<?> catalogo() {
        return ResponseEntity.ok(Map.of("message", "Catálogo público disponible"));
    }

    @GetMapping("/contacto")
    public ResponseEntity<?> contacto() {
        return ResponseEntity.ok(Map.of("message", "Página de contacto funcionando"));
    }

    @GetMapping("/acerca")
    public ResponseEntity<?> acerca() {
        return ResponseEntity.ok(Map.of("message", "Página acerca de nosotros funcionando"));
    }
}
