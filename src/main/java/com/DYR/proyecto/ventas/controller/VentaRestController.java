package com.DYR.proyecto.ventas.controller;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.service.VentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventaService;

    public VentaRestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    @PostMapping
    public Venta crearVenta(@RequestBody Venta venta) {
        return ventaService.guardarVenta(venta);
    }

    @GetMapping("/{id}")
    public Venta obtenerVenta(@PathVariable Long id) {
        return ventaService.buscarVenta(id);
    }

    @PutMapping("/{id}")
    public Venta actualizarVenta(@PathVariable Long id, @RequestBody Venta venta) {
        return ventaService.actualizarVenta(id, venta);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
    }
}
