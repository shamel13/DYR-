package com.DYR.proyecto.movimientoInventario.controller;

import com.DYR.proyecto.movimientoInventario.model.MovimientoInventario;
import com.DYR.proyecto.movimientoInventario.service.MovimientoInventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario/movimientos")
public class MovimientoInventarioMvcController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioMvcController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping("/{inventarioId}")
    public String crearMovimiento(@PathVariable Long inventarioId,
                                  @ModelAttribute MovimientoInventario movimiento) {
        movimientoService.registrarMovimiento(inventarioId, movimiento);
        return "redirect:/inventario/" + inventarioId + "?success=Movimiento registrado";
    }

    @PostMapping("/{inventarioId}/{id}/delete")
    public String eliminarMovimiento(@PathVariable Long inventarioId, @PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return "redirect:/inventario/" + inventarioId + "?success=Movimiento eliminado";
    }
}
