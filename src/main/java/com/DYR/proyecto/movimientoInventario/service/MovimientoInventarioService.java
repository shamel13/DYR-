package com.DYR.proyecto.movimientoInventario.service;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.service.InventarioService;
import com.DYR.proyecto.movimientoInventario.model.MovimientoInventario;
import com.DYR.proyecto.movimientoInventario.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioService inventarioService;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository,
                                       InventarioService inventarioService) {
        this.movimientoRepository = movimientoRepository;
        this.inventarioService = inventarioService;
    }

    public List<MovimientoInventario> listarPorInventario(Long inventarioId) {
        Inventario inv = inventarioService.buscarInventario(inventarioId);
        return movimientoRepository.findByInventario(inv);
    }

    public MovimientoInventario registrarMovimiento(Long inventarioId, MovimientoInventario mov) {
        Inventario inv = inventarioService.buscarInventario(inventarioId);
        mov.setInventario(inv);
        mov.setCreatedAt(LocalDateTime.now());

        // Ajustar stock según tipo
        if ("ENTRADA".equalsIgnoreCase(mov.getTipo())) {
            inv.setStockActual(inv.getStockActual() + mov.getCantidad());
        } else if ("SALIDA".equalsIgnoreCase(mov.getTipo())) {
            inv.setStockActual(Math.max(0, inv.getStockActual() - mov.getCantidad()));
        }
        inv.setUpdatedAt(LocalDateTime.now());

        inventarioService.guardarInventario(inv);
        return movimientoRepository.save(mov);
    }

    public void eliminarMovimiento(Long id) {
        movimientoRepository.deleteById(id);
    }

    public MovimientoInventario buscarMovimiento(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento de inventario no encontrado"));
    }
}
