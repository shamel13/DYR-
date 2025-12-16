package com.DYR.proyecto.pedido.service;

import com.DYR.proyecto.pedido.model.Pedido;
import com.DYR.proyecto.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public Pedido actualizarPedido(Long id, Pedido pedido) {
        Pedido existente = buscarPedido(id);
        existente.setCliente(pedido.getCliente());
        existente.setUser(pedido.getUser());
        existente.setProducto(pedido.getProducto());
        existente.setCantidad(pedido.getCantidad());
        existente.setValorTotal(pedido.getValorTotal());
        return pedidoRepository.save(existente);
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> list() {
        return pedidoRepository.findAll();
    }

    public Pedido solicitarReembolso(Long id) {
        Pedido pedido = obtenerPedidoPorId(id);
        pedido.setEstado("Reembolso");
        return pedidoRepository.save(pedido);
    }
}
