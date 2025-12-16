package com.DYR.proyecto.ventas.service;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.repository.VentaRepository;
import com.DYR.proyecto.pedido.model.Pedido;
import com.DYR.proyecto.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;

    public VentaService(VentaRepository ventaRepository, PedidoRepository pedidoRepository) {
        this.ventaRepository = ventaRepository;
        this.pedidoRepository = pedidoRepository;
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
    
    public Venta solicitarReembolso(Long id) {
        Venta venta = buscarVenta(id);
        venta.setEstado("Reembolso");
        
        // Actualizar el pedido correspondiente si existe
        List<Pedido> pedidos = pedidoRepository.findAll();
        for (Pedido pedido : pedidos) {
            // Buscar el pedido que coincida con la venta
            if (pedido.getCliente() != null && venta.getCliente() != null &&
                pedido.getCliente().getId().equals(venta.getCliente().getId()) &&
                pedido.getProducto() != null && venta.getProducto() != null &&
                pedido.getProducto().getId().equals(venta.getProducto().getId()) &&
                pedido.getCantidad().equals(venta.getCantidad()) &&
                (pedido.getEstado().equals("Completado") || pedido.getEstado().equals("Procesando") || pedido.getEstado().equals("Pendiente"))) {
                // Actualizar el estado del pedido
                pedido.setEstado("Reembolso");
                pedidoRepository.save(pedido);
                break; // Solo actualizar el primer pedido que coincida
            }
        }
        
        return ventaRepository.save(venta);
    }
}
