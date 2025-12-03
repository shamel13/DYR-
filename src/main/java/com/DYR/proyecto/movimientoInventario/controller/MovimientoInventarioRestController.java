package com.DYR.proyecto.movimientoInventario.controller;

import com.DYR.proyecto.movimientoInventario.model.MovimientoInventario;
import com.DYR.proyecto.movimientoInventario.service.MovimientoInventarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario/movimientos")
public class MovimientoInventarioRestController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioRestController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping("/{inventarioId}")
    public List<MovimientoInventario> listar(@PathVariable Long inventarioId) {
        return movimientoService.listarPorInventario(inventarioId);
    }

    @PostMapping("/{inventarioId}")
    public MovimientoInventario crear(@PathVariable Long inventarioId,
                                      @RequestBody MovimientoInventario movimiento) {
        return movimientoService.registrarMovimiento(inventarioId, movimiento);
    }

    @GetMapping("/detalle/{id}")
    public MovimientoInventario obtener(@PathVariable Long id) {
        return movimientoService.buscarMovimiento(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
    }
}
