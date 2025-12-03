package com.DYR.proyecto.inventario.controller;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.service.InventarioService;
import com.DYR.proyecto.movimientoInventario.service.MovimientoInventarioService;
import com.DYR.proyecto.producto.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
public class InventarioMvcController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;
    private final MovimientoInventarioService movimientoService;

    public InventarioMvcController(InventarioService inventarioService,
                                   ProductoService productoService,
                                   MovimientoInventarioService movimientoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("inventarios", inventarioService.listarInventarios());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("inventario", new Inventario());
        return "formularios/inventario/index";
    }

    @PostMapping
    public String store(@ModelAttribute Inventario inventario) {
        inventarioService.guardarInventario(inventario);
        return "redirect:/inventario?success=Inventario registrado correctamente";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Inventario inv = inventarioService.buscarInventario(id);
        model.addAttribute("inventario", inv);
        model.addAttribute("movimientos", movimientoService.listarPorInventario(id));
        return "formularios/inventario/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("inventario", inventarioService.buscarInventario(id));
        model.addAttribute("productos", productoService.listarProductos());
        return "formularios/inventario/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Inventario inventario) {
        inventarioService.actualizarInventario(id, inventario);
        return "redirect:/inventario/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return "redirect:/inventario?success=Inventario eliminado correctamente";
    }
}
