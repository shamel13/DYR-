package com.DYR.proyecto.pedido.controller;

import com.DYR.proyecto.auth.service.UsuarioService;
import com.DYR.proyecto.cliente.service.ClienteService;
import com.DYR.proyecto.pedido.model.Pedido;
import com.DYR.proyecto.pedido.service.PedidoService;
import com.DYR.proyecto.producto.service.ProductoService;
import com.DYR.proyecto.inventario.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@RequestMapping("/pedidos")
public class PedidoPageController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final InventarioService inventarioService;

    public PedidoPageController(PedidoService pedidoService,
                                ClienteService clienteService,
                                ProductoService productoService,
                                UsuarioService usuarioService,
                                InventarioService inventarioService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String cliente,
                       @RequestParam(required = false) String estado,
                       @RequestParam(required = false) String fechaInicio,
                       @RequestParam(required = false) String fechaFin,
                       Model model) {
        var pedidos = pedidoService.listarPedidos().stream()
                .filter(p -> cliente == null || cliente.isBlank() || 
                        (p.getCliente() != null && p.getCliente().getNombre() != null && 
                         p.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase())))
                .filter(p -> estado == null || estado.isBlank() || p.getEstado().equalsIgnoreCase(estado))
                .filter(p -> {
                    if (fechaInicio == null || fechaInicio.isBlank()) return true;
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    return !p.getFechaRegistro().toLocalDate().isBefore(inicio);
                })
                .filter(p -> {
                    if (fechaFin == null || fechaFin.isBlank()) return true;
                    LocalDate fin = LocalDate.parse(fechaFin);
                    return !p.getFechaRegistro().toLocalDate().isAfter(fin);
                })
                .collect(Collectors.toList());
        
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("users", usuarioService.listarUsuarios());
        model.addAttribute("pedido", new Pedido());
        return "formularios/pedidos/index";
    }

    @PostMapping
    public String store(@RequestParam Long clienteId,
                        @RequestParam Long userId,
                        @RequestParam Long productoId,
                        @RequestParam(defaultValue = "1") Integer cantidad,
                        @RequestParam(defaultValue = "0") Double descuento) {
        var cliente = clienteService.buscarCliente(clienteId);
        var user = usuarioService.buscarUsuario(userId);
        var producto = productoService.buscarProducto(productoId);

        // Validar stock
        var inventario = inventarioService.listarInventarios().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);
        
        if (inventario == null || inventario.getStockActual() < cantidad) {
            return "redirect:/pedidos?error=Stock insuficiente";
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUser(user);
        pedido.setProducto(producto);
        pedido.setCantidad(Math.max(1, cantidad));
        double subtotal = producto.getPrecio() * Math.max(1, cantidad);
        double descuentoAplicado = Math.max(0, Math.min(descuento, 100));
        double total = subtotal - (subtotal * descuentoAplicado / 100);
        pedido.setValorTotal(total);
        pedido.setDescuento(descuentoAplicado);
        pedido.setEstado("Pendiente");

        pedidoService.guardarPedido(pedido);
        
        // Reducir stock
        inventario.setStockActual(inventario.getStockActual() - cantidad);
        inventarioService.actualizarInventario(inventario.getId(), inventario);
        
        return "redirect:/pedidos?success=Pedido registrado";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPedido(id));
        return "formularios/pedidos/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPedido(id));
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("users", usuarioService.listarUsuarios());
        return "formularios/pedidos/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long clienteId,
                         @RequestParam Long userId,
                         @RequestParam Long productoId,
                         @RequestParam(defaultValue = "1") Integer cantidad) {
        var cliente = clienteService.buscarCliente(clienteId);
        var user = usuarioService.buscarUsuario(userId);
        var producto = productoService.buscarProducto(productoId);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUser(user);
        pedido.setProducto(producto);
        pedido.setCantidad(Math.max(1, cantidad));
        double total = producto.getPrecio() * Math.max(1, cantidad);
        pedido.setValorTotal(total);

        pedidoService.actualizarPedido(id, pedido);
        return "redirect:/pedidos/" + id + "?success=Pedido actualizado";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return "redirect:/pedidos?success=Pedido eliminado";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        var pedido = pedidoService.buscarPedido(id);
        pedido.setEstado(estado);
        pedidoService.actualizarPedido(id, pedido);
        return "redirect:/pedidos?success=Estado actualizado";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String cliente,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) String fechaInicio,
                          @RequestParam(required = false) String fechaFin,
                          jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var pedidos = pedidoService.listarPedidos().stream()
                .filter(p -> cliente == null || cliente.isBlank() || p.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase()))
                .filter(p -> estado == null || estado.isBlank() || p.getEstado().equalsIgnoreCase(estado))
                .filter(p -> {
                    if (fechaInicio == null || fechaInicio.isBlank()) return true;
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    return !p.getFechaRegistro().toLocalDate().isBefore(inicio);
                })
                .filter(p -> {
                    if (fechaFin == null || fechaFin.isBlank()) return true;
                    LocalDate fin = LocalDate.parse(fechaFin);
                    return !p.getFechaRegistro().toLocalDate().isAfter(fin);
                })
                .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos_reporte.pdf");
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("Reporte de Pedidos").setBold().setFontSize(18));
        document.add(new Paragraph(" "));
        
        Table table = new Table(6);
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Cliente").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));
        
        for (var p : pedidos) {
            table.addCell(p.getId().toString());
            table.addCell(p.getCliente().getNombre());
            table.addCell(p.getProducto().getNombre());
            table.addCell(p.getCantidad().toString());
            table.addCell("$" + String.format("%.2f", p.getValorTotal()));
            table.addCell(p.getEstado());
        }
        
        document.add(table);
        document.close();
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) String cliente,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) String fechaInicio,
                            @RequestParam(required = false) String fechaFin,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var pedidos = pedidoService.listarPedidos().stream()
                .filter(p -> cliente == null || cliente.isBlank() || p.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase()))
                .filter(p -> estado == null || estado.isBlank() || p.getEstado().equalsIgnoreCase(estado))
                .filter(p -> {
                    if (fechaInicio == null || fechaInicio.isBlank()) return true;
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    return !p.getFechaRegistro().toLocalDate().isBefore(inicio);
                })
                .filter(p -> {
                    if (fechaFin == null || fechaFin.isBlank()) return true;
                    LocalDate fin = LocalDate.parse(fechaFin);
                    return !p.getFechaRegistro().toLocalDate().isAfter(fin);
                })
                .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pedidos");
        
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"ID", "Cliente", "Producto", "Cantidad", "Total", "Estado", "Fecha"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (var p : pedidos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getCliente().getNombre());
            row.createCell(2).setCellValue(p.getProducto().getNombre());
            row.createCell(3).setCellValue(p.getCantidad());
            row.createCell(4).setCellValue(p.getValorTotal());
            row.createCell(5).setCellValue(p.getEstado());
            row.createCell(6).setCellValue(p.getFechaRegistro().format(formatter));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
