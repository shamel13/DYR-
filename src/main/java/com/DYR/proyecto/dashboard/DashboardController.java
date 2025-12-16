package com.DYR.proyecto.dashboard;

import com.DYR.proyecto.auth.service.UsuarioService;
import com.DYR.proyecto.cliente.service.ClienteService;
import com.DYR.proyecto.producto.service.ProductoService;
import com.DYR.proyecto.pedido.service.PedidoService;
import com.DYR.proyecto.ventas.service.VentaService;
import com.DYR.proyecto.inventario.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final VentaService ventaService;
    private final InventarioService inventarioService;
    private final UsuarioService usuarioService;

    public DashboardController(ClienteService clienteService, 
                              ProductoService productoService,
                              PedidoService pedidoService,
                              VentaService ventaService,
                              InventarioService inventarioService,
                              UsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.ventaService = ventaService;
        this.inventarioService = inventarioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        // Obtener datos de sesión
        Object currentUserName = session.getAttribute("currentUserName");
        Object currentUserEmail = session.getAttribute("currentUserEmail");
        
        if (currentUserName != null) {
            model.addAttribute("currentUserName", currentUserName.toString());
        }
        if (currentUserEmail != null) {
            model.addAttribute("currentUserEmail", currentUserEmail.toString());
        }
        
        // Obtener estadísticas
        try {
            // Contar usuarios que no son ADMIN (estos son los clientes)
            long totalClientes = usuarioService.listarUsuarios().stream()
                    .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                    .count();
            long totalProductos = productoService.listarProductos().size();
            long totalPedidos = pedidoService.listarPedidos().size();
            
            // Filtrar ventas excluyendo las que están en Reembolso o Cancelado
            long totalVentas = ventaService.listarVentas().stream()
                    .filter(v -> v.getEstado() == null || 
                            (!v.getEstado().equalsIgnoreCase("Reembolso") && 
                             !v.getEstado().equalsIgnoreCase("Cancelado")))
                    .count();
            
            // Calcular ingresos totales excluyendo reembolsos y cancelaciones
            double ingresosVentas = ventaService.listarVentas().stream()
                    .filter(v -> v.getEstado() == null || 
                            (!v.getEstado().equalsIgnoreCase("Reembolso") && 
                             !v.getEstado().equalsIgnoreCase("Cancelado")))
                    .mapToDouble(v -> v.getTotal())
                    .sum();
            
            // Calcular productos con bajo stock (menos de 5 unidades)
            long productosPocoStock = inventarioService.listarInventarios().stream()
                    .filter(i -> i.getStockActual() < 5)
                    .count();
            
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("ingresosVentas", ingresosVentas);
            model.addAttribute("productosPocoStock", productosPocoStock);
            
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
        
        return "dashboard/home";
    }
}

