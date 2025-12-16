package com.DYR.proyecto.ventas.controller;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.ventas.service.VentaService;
import com.DYR.proyecto.cliente.service.ClienteService;
import com.DYR.proyecto.producto.service.ProductoService;
import com.DYR.proyecto.auth.service.UsuarioService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @PostMapping("/{id}/solicitar-reembolso")
    public String solicitarReembolso(@PathVariable Long id) {
        ventaService.solicitarReembolso(id);
        return "redirect:/auth/profile";
    }

    @GetMapping
    public String index(@RequestParam(required = false) String cliente,
                       @RequestParam(required = false) String producto,
                       @RequestParam(required = false) String fechaInicio,
                       @RequestParam(required = false) String fechaFin,
                       @RequestParam(required = false) Double montoMin,
                       @RequestParam(required = false) Double montoMax,
                       Model model) {
        
        List<Venta> ventas = ventaService.listarVentas().stream()
            .filter(v -> cliente == null || cliente.isBlank() || 
                   (v.getCliente() != null && v.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase())))
            .filter(v -> producto == null || producto.isBlank() || 
                   (v.getProducto() != null && v.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase())))
            .filter(v -> fechaInicio == null || fechaInicio.isBlank() || 
                   v.getFechaVenta().toLocalDate().isAfter(LocalDate.parse(fechaInicio).minusDays(1)))
            .filter(v -> fechaFin == null || fechaFin.isBlank() || 
                   v.getFechaVenta().toLocalDate().isBefore(LocalDate.parse(fechaFin).plusDays(1)))
            .filter(v -> montoMin == null || v.getTotal() >= montoMin)
            .filter(v -> montoMax == null || v.getTotal() <= montoMax)
            .collect(Collectors.toList());
        
        model.addAttribute("ventas", ventas);
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

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String cliente,
                         @RequestParam(required = false) String producto,
                         @RequestParam(required = false) String fechaInicio,
                         @RequestParam(required = false) String fechaFin,
                         @RequestParam(required = false) Double montoMin,
                         @RequestParam(required = false) Double montoMax,
                         jakarta.servlet.http.HttpServletResponse response) throws Exception {
        
        List<Venta> ventas = ventaService.listarVentas().stream()
            .filter(v -> cliente == null || cliente.isBlank() || 
                   (v.getCliente() != null && v.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase())))
            .filter(v -> producto == null || producto.isBlank() || 
                   (v.getProducto() != null && v.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase())))
            .filter(v -> fechaInicio == null || fechaInicio.isBlank() || 
                   v.getFechaVenta().toLocalDate().isAfter(LocalDate.parse(fechaInicio).minusDays(1)))
            .filter(v -> fechaFin == null || fechaFin.isBlank() || 
                   v.getFechaVenta().toLocalDate().isBefore(LocalDate.parse(fechaFin).plusDays(1)))
            .filter(v -> montoMin == null || v.getTotal() >= montoMin)
            .filter(v -> montoMax == null || v.getTotal() <= montoMax)
            .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ventas_reporte.pdf");
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.A4.rotate());
        Document document = new Document(pdfDoc);
        
        // Colores corporativos
        com.itextpdf.kernel.colors.Color redColor = new com.itextpdf.kernel.colors.DeviceRgb(220, 38, 38);
        com.itextpdf.kernel.colors.Color darkRed = new com.itextpdf.kernel.colors.DeviceRgb(153, 27, 27);
        com.itextpdf.kernel.colors.Color whiteColor = com.itextpdf.kernel.colors.ColorConstants.WHITE;
        com.itextpdf.kernel.colors.Color lightGray = new com.itextpdf.kernel.colors.DeviceRgb(249, 250, 251);
        com.itextpdf.kernel.colors.Color darkGray = new com.itextpdf.kernel.colors.DeviceRgb(55, 65, 81);
        com.itextpdf.kernel.colors.Color borderColor = new com.itextpdf.kernel.colors.DeviceRgb(229, 231, 235);
        
        // ==== ENCABEZADO CORPORATIVO ====
        float[] headerWidths = {100f, 400f, 100f};
        Table headerTable = new Table(headerWidths);
        headerTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        
        // Logo (ícono simulado)
        Cell logoCell = new Cell()
            .add(new Paragraph("🧥\nDYR").setBold().setFontSize(24).setFontColor(redColor))
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setPadding(10);
        headerTable.addCell(logoCell);
        
        // Información de la empresa
        Cell companyInfoCell = new Cell()
            .add(new Paragraph("DYR - CHAQUETAS DE CUERO SINTÉTICO")
                .setBold().setFontSize(16).setFontColor(darkGray))
            .add(new Paragraph(" Bogotá, Colombia")
                .setFontSize(10).setFontColor(darkGray))
            .add(new Paragraph("| Email: contacto@dyr.com")
                .setFontSize(10).setFontColor(darkGray))
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setPadding(5);
        headerTable.addCell(companyInfoCell);
        
        // Fecha y número
        Cell dateCell = new Cell()
            .add(new Paragraph("FECHA:")
                .setBold().setFontSize(9).setFontColor(darkGray))
            .add(new Paragraph(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFontSize(10).setFontColor(darkGray))
            .add(new Paragraph(" "))
            .add(new Paragraph("N° " + System.currentTimeMillis() % 100000)
                .setBold().setFontSize(11).setFontColor(redColor))
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setPadding(5);
        headerTable.addCell(dateCell);
        
        document.add(headerTable);
        
        // Línea separadora
        com.itextpdf.layout.element.LineSeparator separator = new com.itextpdf.layout.element.LineSeparator(
            new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(2f));
        separator.setStrokeColor(redColor);
        document.add(separator);
        document.add(new Paragraph(" ").setMarginTop(-5));
        
        // Título del reporte con diseño mejorado
        Paragraph titulo = new Paragraph("REPORTE DE VENTAS")
            .setBold()
            .setFontSize(20)
            .setFontColor(whiteColor)
            .setBackgroundColor(darkRed)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setPaddingTop(12)
            .setPaddingBottom(12)
            .setMarginBottom(15);
        document.add(titulo);
        
        // Resumen ejecutivo en tarjetas
        float[] summaryWidths = {200f, 200f, 200f};
        Table summaryTable = new Table(summaryWidths);
        summaryTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        summaryTable.setMarginBottom(20);
        
        Cell totalRegCell = new Cell()
            .add(new Paragraph("TOTAL REGISTROS").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(ventas.size())).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setPadding(15);
        summaryTable.addCell(totalRegCell);
        
        double totalGeneral = ventas.stream().mapToDouble(Venta::getTotal).sum();
        Cell totalVentasCell = new Cell()
            .add(new Paragraph("TOTAL VENTAS").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.format("$%,.0f", totalGeneral)).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setPadding(15);
        summaryTable.addCell(totalVentasCell);
        
        int totalProductos = ventas.stream().mapToInt(Venta::getCantidad).sum();
        Cell totalProdCell = new Cell()
            .add(new Paragraph("PRODUCTOS VENDIDOS").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(totalProductos)).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setPadding(15);
        summaryTable.addCell(totalProdCell);
        
        document.add(summaryTable);
        document.add(new Paragraph(" ").setMarginTop(-10));
        
        // Tabla con diseño mejorado
        float[] columnWidths = {40f, 140f, 140f, 60f, 90f, 100f};
        Table table = new Table(columnWidths);
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        
        // Estilo de encabezado
        String[] headers = {"ID", "Cliente", "Producto", "Cant.", "Total", "Fecha"};
        for (String header : headers) {
            Cell cell = new Cell()
                .add(new Paragraph(header).setBold().setFontSize(11).setFontColor(whiteColor))
                .setBackgroundColor(darkRed)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPaddingTop(10)
                .setPaddingBottom(10);
            table.addHeaderCell(cell);
        }
        
        // Datos con filas alternas y bordes sutiles
        int rowIndex = 0;
        for (var v : ventas) {
            com.itextpdf.kernel.colors.Color rowColor = (rowIndex % 2 == 0) ? whiteColor : lightGray;
            
            Cell idCell = new Cell()
                .add(new Paragraph(String.valueOf(v.getId())).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setPadding(8);
            table.addCell(idCell);
            
            Cell clienteCell = new Cell()
                .add(new Paragraph(v.getCliente() != null ? v.getCliente().getNombre() : "N/A").setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8);
            table.addCell(clienteCell);
            
            Cell productoCell = new Cell()
                .add(new Paragraph(v.getProducto() != null ? v.getProducto().getNombre() : "N/A").setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8);
            table.addCell(productoCell);
            
            Cell cantCell = new Cell()
                .add(new Paragraph(String.valueOf(v.getCantidad())).setFontSize(9).setBold())
                .setBackgroundColor(rowColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setPadding(8);
            table.addCell(cantCell);
            
            Cell totalCell = new Cell()
                .add(new Paragraph(String.format("$%,.0f", v.getTotal())).setFontSize(9).setBold().setFontColor(redColor))
                .setBackgroundColor(rowColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setPadding(8);
            table.addCell(totalCell);
            
            Cell fechaCell = new Cell()
                .add(new Paragraph(v.getFechaVenta().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setPadding(8);
            table.addCell(fechaCell);
            
            rowIndex++;
        }
        
        document.add(table);
        
        // Total con diseño destacado
        document.add(new Paragraph(" ").setMarginTop(5));
        Table totalTable = new Table(new float[]{420f, 150f});
        totalTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        
        Cell totalLabelCell = new Cell()
            .add(new Paragraph("TOTAL GENERAL:").setBold().setFontSize(14).setFontColor(whiteColor))
            .setBackgroundColor(darkRed)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
            .setPadding(12);
        totalTable.addCell(totalLabelCell);
        
        Cell totalValueCell = new Cell()
            .add(new Paragraph(String.format("$%,.0f", totalGeneral)).setBold().setFontSize(16).setFontColor(whiteColor))
            .setBackgroundColor(redColor)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setPadding(12);
        totalTable.addCell(totalValueCell);
        
        document.add(totalTable);
        
        // Pie de página
        document.add(new Paragraph(" ").setMarginTop(20));
        com.itextpdf.layout.element.LineSeparator footerSeparator = new com.itextpdf.layout.element.LineSeparator(
            new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f));
        footerSeparator.setStrokeColor(borderColor);
        document.add(footerSeparator);
        
        Paragraph footer = new Paragraph()
            .add("DYR - Chaquetas de Cuero Sintético | www.dyr.com | © " + java.time.Year.now().getValue())
            .setFontSize(8)
            .setFontColor(darkGray)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
            .setMarginTop(5);
        document.add(footer);
        
        document.close();
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) String cliente,
                           @RequestParam(required = false) String producto,
                           @RequestParam(required = false) String fechaInicio,
                           @RequestParam(required = false) String fechaFin,
                           @RequestParam(required = false) Double montoMin,
                           @RequestParam(required = false) Double montoMax,
                           jakarta.servlet.http.HttpServletResponse response) throws Exception {
        
        List<Venta> ventas = ventaService.listarVentas().stream()
            .filter(v -> cliente == null || cliente.isBlank() || 
                   (v.getCliente() != null && v.getCliente().getNombre().toLowerCase().contains(cliente.toLowerCase())))
            .filter(v -> producto == null || producto.isBlank() || 
                   (v.getProducto() != null && v.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase())))
            .filter(v -> fechaInicio == null || fechaInicio.isBlank() || 
                   v.getFechaVenta().toLocalDate().isAfter(LocalDate.parse(fechaInicio).minusDays(1)))
            .filter(v -> fechaFin == null || fechaFin.isBlank() || 
                   v.getFechaVenta().toLocalDate().isBefore(LocalDate.parse(fechaFin).plusDays(1)))
            .filter(v -> montoMin == null || v.getTotal() >= montoMin)
            .filter(v -> montoMax == null || v.getTotal() <= montoMax)
            .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ventas_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ventas");
        
        // ==== ESTILOS PROFESIONALES ====
        // Estilo para el logo/título principal
        CellStyle logoStyle = workbook.createCellStyle();
        Font logoFont = workbook.createFont();
        logoFont.setBold(true);
        logoFont.setFontHeightInPoints((short) 20);
        logoFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        logoStyle.setFont(logoFont);
        logoStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        logoStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        
        // Estilo para información de la empresa
        CellStyle companyStyle = workbook.createCellStyle();
        Font companyFont = workbook.createFont();
        companyFont.setFontHeightInPoints((short) 10);
        companyStyle.setFont(companyFont);
        companyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        
        // Estilo para título del reporte
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        titleStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        
        // Estilo para tarjetas de resumen
        CellStyle summaryLabelStyle = workbook.createCellStyle();
        Font summaryLabelFont = workbook.createFont();
        summaryLabelFont.setBold(true);
        summaryLabelFont.setFontHeightInPoints((short) 9);
        summaryLabelStyle.setFont(summaryLabelFont);
        summaryLabelStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        summaryLabelStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        summaryLabelStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        summaryLabelStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryLabelStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryLabelStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryLabelStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        
        CellStyle summaryValueStyle = workbook.createCellStyle();
        Font summaryValueFont = workbook.createFont();
        summaryValueFont.setBold(true);
        summaryValueFont.setFontHeightInPoints((short) 18);
        summaryValueFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        summaryValueStyle.setFont(summaryValueFont);
        summaryValueStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        summaryValueStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        summaryValueStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        summaryValueStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryValueStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryValueStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        summaryValueStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        
        // Estilo para encabezados de columnas
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_RED.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        
        // Estilos para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        
        CellStyle alternateStyle = workbook.createCellStyle();
        alternateStyle.cloneStyleFrom(dataStyle);
        alternateStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        alternateStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.cloneStyleFrom(dataStyle);
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0"));
        Font moneyFont = workbook.createFont();
        moneyFont.setBold(true);
        moneyFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        moneyStyle.setFont(moneyFont);
        moneyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
        
        CellStyle totalStyle = workbook.createCellStyle();
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        totalFont.setFontHeightInPoints((short) 14);
        totalStyle.setFont(totalFont);
        totalStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        totalStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0"));
        totalStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        totalStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THICK);
        totalStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THICK);
        totalStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THICK);
        totalStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THICK);
        
        // ==== ENCABEZADO CORPORATIVO ====
        int currentRow = 0;
        
        // Logo y nombre de la empresa
        Row logoRow = sheet.createRow(currentRow++);
        logoRow.setHeight((short) 600);
        org.apache.poi.ss.usermodel.Cell logoCell = logoRow.createCell(0);
        logoCell.setCellValue("🧥 DYR");
        logoCell.setCellStyle(logoStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 5));
        
        // Información de la empresa
        Row companyRow1 = sheet.createRow(currentRow++);
        org.apache.poi.ss.usermodel.Cell companyCell1 = companyRow1.createCell(0);
        companyCell1.setCellValue("CHAQUETAS DE CUERO SINTÉTICO");
        companyCell1.setCellStyle(companyStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 5));
        
        Row companyRow2 = sheet.createRow(currentRow++);
        org.apache.poi.ss.usermodel.Cell companyCell2 = companyRow2.createCell(0);
        companyCell2.setCellValue("Calle 123 #45-67, Bogotá | Tel: (601) 234-5678 | contacto@dyr.com");
        companyCell2.setCellStyle(companyStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 5));
        
        currentRow++; // Espacio
        
        // Título del reporte
        Row titleRow = sheet.createRow(currentRow++);
        titleRow.setHeight((short) 500);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE VENTAS");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 5));
        
        currentRow++; // Espacio
        
        // Fecha y número de reporte
        Row infoRow = sheet.createRow(currentRow++);
        org.apache.poi.ss.usermodel.Cell dateCell = infoRow.createCell(0);
        dateCell.setCellValue("Fecha: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        org.apache.poi.ss.usermodel.Cell numCell = infoRow.createCell(4);
        numCell.setCellValue("N° " + System.currentTimeMillis() % 100000);
        
        currentRow++; // Espacio
        
        // ==== RESUMEN EJECUTIVO ====
        Row summaryTitleRow = sheet.createRow(currentRow++);
        org.apache.poi.ss.usermodel.Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("RESUMEN EJECUTIVO");
        summaryTitleCell.setCellStyle(summaryLabelStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 5));
        
        // Tarjetas de resumen
        Row summaryLabelsRow = sheet.createRow(currentRow++);
        org.apache.poi.ss.usermodel.Cell totalRegLabel = summaryLabelsRow.createCell(0);
        totalRegLabel.setCellValue("TOTAL REGISTROS");
        totalRegLabel.setCellStyle(summaryLabelStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 1));
        
        org.apache.poi.ss.usermodel.Cell totalVentasLabel = summaryLabelsRow.createCell(2);
        totalVentasLabel.setCellValue("TOTAL VENTAS");
        totalVentasLabel.setCellStyle(summaryLabelStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 2, 3));
        
        org.apache.poi.ss.usermodel.Cell totalProdLabel = summaryLabelsRow.createCell(4);
        totalProdLabel.setCellValue("PRODUCTOS VENDIDOS");
        totalProdLabel.setCellStyle(summaryLabelStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 4, 5));
        
        Row summaryValuesRow = sheet.createRow(currentRow++);
        summaryValuesRow.setHeight((short) 700);
        
        double totalGeneral = ventas.stream().mapToDouble(Venta::getTotal).sum();
        int totalProductos = ventas.stream().mapToInt(Venta::getCantidad).sum();
        
        org.apache.poi.ss.usermodel.Cell totalRegValue = summaryValuesRow.createCell(0);
        totalRegValue.setCellValue(ventas.size());
        totalRegValue.setCellStyle(summaryValueStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 0, 1));
        
        org.apache.poi.ss.usermodel.Cell totalVentasValue = summaryValuesRow.createCell(2);
        totalVentasValue.setCellValue(totalGeneral);
        CellStyle summaryMoneyStyle = workbook.createCellStyle();
        summaryMoneyStyle.cloneStyleFrom(summaryValueStyle);
        summaryMoneyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0"));
        totalVentasValue.setCellStyle(summaryMoneyStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 2, 3));
        
        org.apache.poi.ss.usermodel.Cell totalProdValue = summaryValuesRow.createCell(4);
        totalProdValue.setCellValue(totalProductos);
        totalProdValue.setCellStyle(summaryValueStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow-1, currentRow-1, 4, 5));
        
        currentRow += 2; // Espacio
        
        // ==== TABLA DE DATOS ====
        Row headerRow = sheet.createRow(currentRow++);
        headerRow.setHeight((short) 400);
        String[] headers = {"ID", "Cliente", "Producto", "Cant.", "Total", "Fecha"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos con filas alternas
        for (var v : ventas) {
            Row row = sheet.createRow(currentRow);
            CellStyle currentStyle = (currentRow % 2 == 0) ? dataStyle : alternateStyle;
            
            org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
            cell0.setCellValue(v.getId());
            cell0.setCellStyle(currentStyle);
            
            org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
            cell1.setCellValue(v.getCliente() != null ? v.getCliente().getNombre() : "N/A");
            cell1.setCellStyle(currentStyle);
            
            org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
            cell2.setCellValue(v.getProducto() != null ? v.getProducto().getNombre() : "N/A");
            cell2.setCellStyle(currentStyle);
            
            org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
            cell3.setCellValue(v.getCantidad());
            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(currentStyle);
            centerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            cell3.setCellStyle(centerStyle);
            
            org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
            cell4.setCellValue(v.getTotal());
            CellStyle moneyStyleAlt = workbook.createCellStyle();
            moneyStyleAlt.cloneStyleFrom(moneyStyle);
            if (currentRow % 2 != 0) {
                moneyStyleAlt.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
                moneyStyleAlt.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            }
            cell4.setCellStyle(moneyStyleAlt);
            
            org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
            cell5.setCellValue(v.getFechaVenta().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(currentStyle);
            dateStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            cell5.setCellStyle(dateStyle);
            
            currentRow++;
        }
        
        // Fila de total
        currentRow += 1;
        Row totalRow = sheet.createRow(currentRow);
        totalRow.setHeight((short) 500);
        
        org.apache.poi.ss.usermodel.Cell totalLabelCell = totalRow.createCell(3);
        totalLabelCell.setCellValue("TOTAL GENERAL:");
        CellStyle totalLabelStyle = workbook.createCellStyle();
        totalLabelStyle.cloneStyleFrom(totalStyle);
        totalLabelStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
        totalLabelStyle.setDataFormat((short) 0);
        totalLabelCell.setCellStyle(totalLabelStyle);
        
        org.apache.poi.ss.usermodel.Cell totalValueCell = totalRow.createCell(4);
        totalValueCell.setCellValue(totalGeneral);
        totalValueCell.setCellStyle(totalStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow, currentRow, 4, 5));
        
        // Ajustar anchos de columna
        sheet.setColumnWidth(0, 2000);  // ID
        sheet.setColumnWidth(1, 8000);  // Cliente
        sheet.setColumnWidth(2, 8000);  // Producto
        sheet.setColumnWidth(3, 3000);  // Cantidad
        sheet.setColumnWidth(4, 4000);  // Total
        sheet.setColumnWidth(5, 5000);  // Fecha
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
