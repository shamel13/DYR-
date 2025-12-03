package com.DYR.proyecto.cliente.controller;

import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.cliente.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteMvcController {

    private final ClienteService clienteService;

    public ClienteMvcController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("cliente", new Cliente());
        return "formularios/clientes/index";
    }

    @PostMapping
    public String store(@ModelAttribute Cliente cliente) {
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes?success=Cliente registrado correctamente";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarCliente(id));
        model.addAttribute("pedidos", clienteService.listarPedidosPorCliente(id));
        return "formularios/clientes/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarCliente(id));
        return "formularios/clientes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        clienteService.actualizarCliente(id, cliente);
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "redirect:/clientes?success=Cliente eliminado correctamente";
    }
}
