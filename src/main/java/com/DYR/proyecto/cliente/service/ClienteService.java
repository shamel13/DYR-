package com.DYR.proyecto.cliente.service;

import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente actualizarCliente(Long id, Cliente cliente) {
        Cliente existente = buscarCliente(id);
        existente.setNombre(cliente.getNombre());
        existente.setEmail(cliente.getEmail());
        existente.setTelefono(cliente.getTelefono());
        existente.setDireccion(cliente.getDireccion());
        existente.setCiudad(cliente.getCiudad());
        return clienteRepository.save(existente);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    // Si tienes relación con pedidos
    public List<?> listarPedidosPorCliente(Long idCliente) {
        Cliente cliente = buscarCliente(idCliente);
        return cliente.getPedidos();
    }
}
