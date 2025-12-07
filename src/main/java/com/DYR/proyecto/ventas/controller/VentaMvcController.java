package com.DYR.proyecto.ventas.controller;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.service.VentaService;
import com.DYR.proyecto.cliente.service.ClienteService;
import com.DYR.proyecto.producto.service.ProductoService;
import com.DYR.proyecto.auth.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ventas")
public class VentaMvcController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public VentaMvcController(VentaService ventaService,
                              ClienteService clienteService,
                              ProductoService productoService,
                              UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("ventas", ventaService.listarVentas());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("users", usuarioService.listarUsuarios());
        return "formularios/ventas/index";
    }

    @PostMapping
    public String store(@RequestParam Long clienteId,
                        @RequestParam Long userId,
                        @RequestParam Long productoId,
                        @RequestParam(defaultValue = "1") Integer cantidad,
                        @RequestParam(required = false) java.time.LocalDate fechaVenta) {
        var cliente = clienteService.buscarCliente(clienteId);
        var user = usuarioService.buscarUsuario(userId);
        var producto = productoService.buscarProducto(productoId);
        Venta v = new Venta();
        v.setCliente(cliente);
        v.setUser(user);
        v.setProducto(producto);
        v.setCantidad(cantidad);
        double total = producto.getPrecio() * Math.max(1, cantidad);
        v.setTotal(total);
        if (fechaVenta != null) {
            v.setFechaVenta(fechaVenta.atStartOfDay());
        }
        ventaService.guardarVenta(v);
        return "redirect:/ventas?success=Venta registrada correctamente";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("venta", ventaService.buscarVenta(id));
        return "formularios/ventas/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("venta", ventaService.buscarVenta(id));
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("users", usuarioService.listarUsuarios());
        return "formularios/ventas/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long clienteId,
                         @RequestParam Long userId,
                         @RequestParam Long productoId,
                         @RequestParam(defaultValue = "1") Integer cantidad,
                         @RequestParam(required = false) java.time.LocalDate fechaVenta) {
        var cliente = clienteService.buscarCliente(clienteId);
        var user = usuarioService.buscarUsuario(userId);
        var producto = productoService.buscarProducto(productoId);
        Venta v = new Venta();
        v.setCliente(cliente);
        v.setUser(user);
        v.setProducto(producto);
        v.setCantidad(cantidad);
        double total = producto.getPrecio() * Math.max(1, cantidad);
        v.setTotal(total);
        if (fechaVenta != null) {
            v.setFechaVenta(fechaVenta.atStartOfDay());
        }
        ventaService.actualizarVenta(id, v);
        return "redirect:/ventas/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return "redirect:/ventas?success=Venta eliminada correctamente";
    }
}
