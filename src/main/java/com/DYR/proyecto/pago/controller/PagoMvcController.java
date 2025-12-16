package com.DYR.proyecto.pago.controller;

import com.DYR.proyecto.pago.model.Pago;
import com.DYR.proyecto.pago.service.PagoService;
import com.DYR.proyecto.auth.service.UsuarioService;
import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.cliente.service.ClienteService;
import com.DYR.proyecto.pedido.model.Pedido;
import com.DYR.proyecto.pedido.service.PedidoService;
import com.DYR.proyecto.producto.model.Producto;
import com.DYR.proyecto.producto.service.ProductoService;
import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.service.VentaService;
import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

@Controller
@RequestMapping("/pagos")
public class PagoMvcController {

    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProductoService productoService;
    private final VentaService ventaService;
    private final InventarioService inventarioService;

    public PagoMvcController(PagoService pagoService, UsuarioService usuarioService,
                            ClienteService clienteService, PedidoService pedidoService,
                            ProductoService productoService, VentaService ventaService,
                            InventarioService inventarioService) {
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pagos", pagoService.listarPagos());
        return "formularios/pagos/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Double monto,
                       @SessionAttribute(name = "username", required = false) String currentUsername,
                       Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("monto", monto);

        // Resolver usuario autenticado (session attribute o SecurityContext)
        String resolvedUsername = resolveUsername(currentUsername);
        if (resolvedUsername != null) {
            var usuario = usuarioService.buscarPorUsername(resolvedUsername);
            if (usuario != null) {
                model.addAttribute("currentUser", usuario);
            }
        }

        return "formularios/pagos/nuevo";
    }

    @PostMapping
    public String store(@RequestParam Long usuarioId,
                       @RequestParam Double monto,
                       @RequestParam String metodoPago,
                       @RequestParam(required = false) String carrito,
                       @RequestParam(required = false) String telefono,
                       @RequestParam(required = false) String emailContacto,
                       @RequestParam(required = false) String direccion,
                       @RequestParam(required = false) String ciudad,
                       @RequestParam(required = false) String codigoPostal,
                       @RequestParam(required = false) String descripcion,
                       @RequestParam(required = false) String numeroTarjeta,
                       @RequestParam(required = false) String nombreTitular,
                       @RequestParam(required = false) String tipoTarjeta,
                       @RequestParam(required = false) String emailPaypal,
                       @RequestParam(required = false) String transactionIdPaypal,
                       @RequestParam(required = false) String bancoCuenta,
                       @RequestParam(required = false) String numeroTransferencia,
                       @SessionAttribute(name = "username", required = false) String currentUsername,
                       Model model) {
        
        // Crear objeto Pago manualmente
        Pago pago = new Pago();
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setDescripcion(descripcion);
        pago.setTelefono(telefono);
        pago.setEmailContacto(emailContacto);
        pago.setDireccion(direccion);
        pago.setCiudad(ciudad);
        pago.setCodigoPostal(codigoPostal);
        
        // Datos específicos por método de pago
        if ("Tarjeta".equals(metodoPago)) {
            pago.setNombreTitular(nombreTitular);
            pago.setTipoTarjeta(tipoTarjeta);
            if (numeroTarjeta != null && !numeroTarjeta.isEmpty()) {
                String numero = numeroTarjeta.replaceAll("\\s", "");
                if (numero.length() >= 4) {
                    pago.setNumeroTarjeta("****" + numero.substring(numero.length() - 4));
                }
            }
        } else if ("PayPal".equals(metodoPago)) {
            pago.setEmailPaypal(emailPaypal);
            pago.setTransactionIdPaypal(transactionIdPaypal);
        } else if ("Transferencia".equals(metodoPago)) {
            pago.setBancoCuenta(bancoCuenta);
            pago.setNumeroTransferencia(numeroTransferencia);
        }
        
        // Asignar usuario
        var usuario = usuarioService.buscarUsuario(usuarioId);
        pago.setUsuario(usuario);
        
        // Rellenar datos de contacto y dirección si no se enviaron
        if (usuario != null) {
            if (pago.getTelefono() == null || pago.getTelefono().isEmpty()) {
                pago.setTelefono(usuario.getPhone());
            }
            if (pago.getEmailContacto() == null || pago.getEmailContacto().isEmpty()) {
                pago.setEmailContacto(usuario.getEmail());
            }
            if (pago.getDireccion() == null || pago.getDireccion().isEmpty()) {
                pago.setDireccion(usuario.getAddress());
            }
            if (pago.getCiudad() == null || pago.getCiudad().isEmpty()) {
                pago.setCiudad(usuario.getCity());
            }
            if (pago.getCodigoPostal() == null || pago.getCodigoPostal().isEmpty()) {
                pago.setCodigoPostal(usuario.getPostalCode());
            }
        }
        
        Pago pagoGuardado = pagoService.guardarPago(pago);
        
        // Crear cliente, pedidos y ventas automáticamente
        try {
            Cliente cliente = buscarOCrearCliente(pagoGuardado);
            crearPedidosYVentasDesdeCarrito(pagoGuardado, cliente, carrito);
        } catch (Exception e) {
            System.err.println("Error al crear pedidos y ventas: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Redirigir a página de confirmación con datos del pago
        model.addAttribute("numeroReferencia", pagoGuardado.getNumeroReferencia());
        model.addAttribute("monto", pagoGuardado.getMonto());
        return "formularios/pagos/confirmacion";
    }

    private String resolveUsername(String sessionUsername) {
        if (sessionUsername != null && !sessionUsername.isBlank()) {
            return sessionUsername;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equalsIgnoreCase(auth.getName())) {
            return auth.getName();
        }
        return null;
    }

    private void crearPedidosYVentasDesdeCarrito(Pago pago, Cliente cliente, String carritoJson) {
        if (carritoJson == null || carritoJson.isBlank()) {
            System.err.println("No se recibió información del carrito");
            return;
        }
        
        try {
            // Parsear JSON del carrito (formato: [{id:1, nombre:"X", precio:100, cantidad:2}, ...])
            carritoJson = carritoJson.trim();
            
            if (!carritoJson.startsWith("[")) {
                System.err.println("Formato de carrito inválido");
                return;
            }
            
            // Remover corchetes y dividir por objetos
            carritoJson = carritoJson.substring(1, carritoJson.length() - 1);
            String[] items = carritoJson.split("\\},\\{");
            
            for (String item : items) {
                item = item.replace("{", "").replace("}", "");
                String[] campos = item.split(",");
                
                Long productoId = null;
                Integer cantidad = 1;
                Double precio = 0.0;
                
                for (String campo : campos) {
                    String[] partes = campo.split(":");
                    if (partes.length != 2) continue;
                    
                    String clave = partes[0].trim().replace("\"", "");
                    String valor = partes[1].trim().replace("\"", "");
                    
                    if ("id".equals(clave)) {
                        productoId = Long.parseLong(valor);
                    } else if ("cantidad".equals(clave)) {
                        cantidad = Integer.parseInt(valor);
                    } else if ("precio".equals(clave)) {
                        precio = Double.parseDouble(valor);
                    }
                }
                
                if (productoId != null) {
                    Producto producto = productoService.buscarProducto(productoId);
                    if (producto != null) {
                        Double valorTotal = precio * cantidad;
                        
                        // Crear Pedido
                        Pedido pedido = new Pedido();
                        pedido.setCliente(cliente);
                        pedido.setUser(pago.getUsuario());
                        pedido.setProducto(producto);
                        pedido.setCantidad(cantidad);
                        pedido.setValorTotal(valorTotal);
                        pedido.setEstado("Completado");
                        pedido.setDescuento(0.0);
                        
                        // Guardar información de envío en el pedido
                        pedido.setShippingAddress(pago.getDireccion());
                        pedido.setShippingCity(pago.getCiudad());
                        pedido.setShippingPostalCode(pago.getCodigoPostal());
                        pedido.setShippingPhone(pago.getTelefono());
                        pedido.setShippingEmail(pago.getEmailContacto());
                        
                        pedidoService.guardarPedido(pedido);
                        
                        // Crear Venta
                        Venta venta = new Venta();
                        venta.setCliente(cliente);
                        venta.setUser(pago.getUsuario());
                        venta.setProducto(producto);
                        venta.setCantidad(cantidad);
                        venta.setTotal(valorTotal);
                        ventaService.guardarVenta(venta);
                        
                        // Actualizar Stock del Inventario
                        try {
                            Inventario inventario = inventarioService.buscarPorProducto(producto);
                            int nuevoStock = inventario.getStockActual() - cantidad;
                            if (nuevoStock < 0) {
                                System.err.println("ADVERTENCIA: Stock negativo para producto: " + producto.getNombre());
                                nuevoStock = 0;
                            }
                            inventario.setStockActual(nuevoStock);
                            inventarioService.guardarInventario(inventario);
                        } catch (Exception e) {
                            System.err.println("Error al actualizar stock del producto " + producto.getNombre() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear carrito: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Cliente buscarOCrearCliente(Pago pago) {
        // Buscar cliente por email
        var clienteExistente = clienteService.listarClientes().stream()
            .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(pago.getEmailContacto()))
            .findFirst();
            
        if (clienteExistente.isPresent()) {
            return clienteExistente.get();
        }
        
        // Crear nuevo cliente con datos del pago (usar username del usuario)
        Cliente nuevoCliente = new Cliente();
        String nombreCliente = pago.getUsuario().getUsername() != null ? 
                              pago.getUsuario().getUsername() : 
                              pago.getUsuario().getName();
        nuevoCliente.setNombre(nombreCliente);
        nuevoCliente.setEmail(pago.getEmailContacto());
        nuevoCliente.setTelefono(pago.getTelefono());
        nuevoCliente.setDireccion(pago.getDireccion());
        nuevoCliente.setCiudad(pago.getCiudad());
        
        return clienteService.guardarCliente(nuevoCliente);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("pago", pagoService.buscarPago(id));
        return "formularios/pagos/show";
    }

    @PostMapping("/{id}/procesar")
    public String procesar(@PathVariable Long id) {
        pagoService.procesarPago(pagoService.buscarPago(id));
        return "redirect:/pagos?success=Pago procesado";
    }

    @PostMapping("/{id}/reembolsar")
    public String reembolsar(@PathVariable Long id) {
        Pago pago = pagoService.reembolsarPago(id);
        if (pago != null) {
            return "redirect:/pagos?success=Pago reembolsado";
        }
        return "redirect:/pagos?error=No se pudo reembolsar el pago";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return "redirect:/pagos?success=Pago eliminado";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String metodo,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) String fechaInicio,
                          @RequestParam(required = false) String fechaFin,
                          jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var pagos = pagoService.listarPagos().stream()
                .filter(p -> metodo == null || metodo.isBlank() || p.getMetodoPago().equalsIgnoreCase(metodo))
                .filter(p -> estado == null || estado.isBlank() || p.getEstado().equalsIgnoreCase(estado))
                .filter(p -> {
                    if (fechaInicio == null || fechaInicio.isBlank()) return true;
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    return !p.getFechaPago().toLocalDate().isBefore(inicio);
                })
                .filter(p -> {
                    if (fechaFin == null || fechaFin.isBlank()) return true;
                    LocalDate fin = LocalDate.parse(fechaFin);
                    return !p.getFechaPago().toLocalDate().isAfter(fin);
                })
                .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pagos_reporte.pdf");
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("Reporte de Pagos").setBold().setFontSize(18));
        document.add(new Paragraph(" "));
        
        Table table = new Table(5);
        table.addHeaderCell(new Cell().add(new Paragraph("Referencia").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Usuario").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Método").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Monto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));
        
        for (var p : pagos) {
            table.addCell(p.getNumeroReferencia());
            table.addCell(p.getUsuario() != null ? p.getUsuario().getName() : "N/A");
            table.addCell(p.getMetodoPago());
            table.addCell("$" + String.format("%.2f", p.getMonto()));
            table.addCell(p.getEstado());
        }
        
        document.add(table);
        document.close();
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) String metodo,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) String fechaInicio,
                            @RequestParam(required = false) String fechaFin,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var pagos = pagoService.listarPagos().stream()
                .filter(p -> metodo == null || metodo.isBlank() || p.getMetodoPago().equalsIgnoreCase(metodo))
                .filter(p -> estado == null || estado.isBlank() || p.getEstado().equalsIgnoreCase(estado))
                .filter(p -> {
                    if (fechaInicio == null || fechaInicio.isBlank()) return true;
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    return !p.getFechaPago().toLocalDate().isBefore(inicio);
                })
                .filter(p -> {
                    if (fechaFin == null || fechaFin.isBlank()) return true;
                    LocalDate fin = LocalDate.parse(fechaFin);
                    return !p.getFechaPago().toLocalDate().isAfter(fin);
                })
                .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pagos_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos");
        
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"ID", "Referencia", "Usuario", "Método", "Monto", "Estado", "Fecha"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (var p : pagos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getNumeroReferencia());
            row.createCell(2).setCellValue(p.getUsuario() != null ? p.getUsuario().getName() : "N/A");
            row.createCell(3).setCellValue(p.getMetodoPago());
            row.createCell(4).setCellValue(p.getMonto());
            row.createCell(5).setCellValue(p.getEstado());
            row.createCell(6).setCellValue(p.getFechaPago().format(formatter));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
