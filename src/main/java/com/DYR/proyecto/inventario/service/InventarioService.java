package com.DYR.proyecto.inventario.service;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.repository.InventarioRepository;
import com.DYR.proyecto.producto.model.Producto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listarInventarios() {
        return inventarioRepository.findAll();
    }

    public Inventario guardarInventario(Inventario inventario) {
        inventario.setUpdatedAt(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }

    public Inventario buscarInventario(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
    }

    public Inventario actualizarInventario(Long id, Inventario data) {
        Inventario inv = buscarInventario(id);
        inv.setProducto(data.getProducto());
        inv.setStockActual(data.getStockActual());
        inv.setUpdatedAt(LocalDateTime.now());
        return inventarioRepository.save(inv);
    }

    public void eliminarInventario(Long id) {
        inventarioRepository.deleteById(id);
    }

    public Inventario buscarPorProducto(Producto producto) {
        return inventarioRepository.findByProducto(producto)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para el producto"));
    }

    public int obtenerStockPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .map(Inventario::getStockActual)
                .orElse(0);
    }
}
