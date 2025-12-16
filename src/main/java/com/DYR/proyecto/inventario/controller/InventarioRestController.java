package com.DYR.proyecto.inventario.controller;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.service.InventarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioRestController {

    private final InventarioService inventarioService;

    public InventarioRestController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<Inventario> listarInventarios() {
        return inventarioService.listarInventarios();
    }

    @PostMapping
    public Inventario crearInventario(@RequestBody Inventario inventario) {
        return inventarioService.guardarInventario(inventario);
    }

    @GetMapping("/{id}")
    public Inventario obtenerInventario(@PathVariable Long id) {
        return inventarioService.buscarInventario(id);
    }

    @PutMapping("/{id}")
    public Inventario actualizarInventario(@PathVariable Long id, @RequestBody Inventario inventario) {
        return inventarioService.actualizarInventario(id, inventario);
    }

    @DeleteMapping("/{id}")
    public void eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
    }
}
