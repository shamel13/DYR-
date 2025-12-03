package com.DYR.proyecto.ventas.service;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    public Venta buscarVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    public Venta actualizarVenta(Long id, Venta venta) {
        Venta existente = buscarVenta(id);
        existente.setCliente(venta.getCliente());
        existente.setUser(venta.getUser());
        existente.setProducto(venta.getProducto());
        existente.setCantidad(venta.getCantidad());
        existente.setTotal(venta.getTotal());
        return ventaRepository.save(existente);
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }
}
