package com.DYR.proyecto.inventario.controller;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.inventario.service.InventarioService;
import com.DYR.proyecto.movimientoInventario.service.MovimientoInventarioService;
import com.DYR.proyecto.producto.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
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

@Controller
@RequestMapping("/inventario")
public class InventarioMvcController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;
    private final MovimientoInventarioService movimientoService;

    public InventarioMvcController(InventarioService inventarioService,
                                   ProductoService productoService,
                                   MovimientoInventarioService movimientoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String producto,
                       @RequestParam(required = false) Integer stockMin,
                       @RequestParam(required = false) Integer stockMax,
                       Model model) {
        var inventarios = inventarioService.listarInventarios().stream()
                .filter(i -> producto == null || producto.isBlank() || i.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase()))
                .filter(i -> stockMin == null || i.getStockActual() >= stockMin)
                .filter(i -> stockMax == null || i.getStockActual() <= stockMax)
                .collect(Collectors.toList());
        
        model.addAttribute("inventarios", inventarios);
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("inventario", new Inventario());
        return "formularios/inventario/index";
    }

    @PostMapping
    public String store(@ModelAttribute Inventario inventario) {
        inventarioService.guardarInventario(inventario);
        return "redirect:/inventario?success=Inventario registrado correctamente";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Inventario inv = inventarioService.buscarInventario(id);
        model.addAttribute("inventario", inv);
        model.addAttribute("movimientos", movimientoService.listarPorInventario(id));
        return "formularios/inventario/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("inventario", inventarioService.buscarInventario(id));
        model.addAttribute("productos", productoService.listarProductos());
        return "formularios/inventario/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long productoId,
                         @RequestParam Integer stockActual) {
        var inventario = inventarioService.buscarInventario(id);
        var producto = productoService.buscarProducto(productoId);
        inventario.setProducto(producto);
        inventario.setStockActual(stockActual);
        inventarioService.actualizarInventario(id, inventario);
        return "redirect:/inventario/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return "redirect:/inventario?success=Inventario eliminado correctamente";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String producto,
                          @RequestParam(required = false) Integer stockMin,
                          @RequestParam(required = false) Integer stockMax,
                          jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var inventarios = inventarioService.listarInventarios().stream()
                .filter(i -> producto == null || producto.isBlank() || i.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase()))
                .filter(i -> stockMin == null || i.getStockActual() >= stockMin)
                .filter(i -> stockMax == null || i.getStockActual() <= stockMax)
                .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_reporte.pdf");
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDoc);
        
        // Colores corporativos
        DeviceRgb redColor = new DeviceRgb(220, 38, 38);
        DeviceRgb darkRed = new DeviceRgb(153, 27, 27);
        DeviceRgb lightGray = new DeviceRgb(249, 250, 251);
        DeviceRgb darkGray = new DeviceRgb(55, 65, 81);
        
        // Encabezado corporativo
        float[] headerWidths = {100f, 400f, 100f};
        Table headerTable = new Table(headerWidths);
        headerTable.setWidth(UnitValue.createPercentValue(100));
        
        headerTable.addCell(new Cell()
            .add(new Paragraph("🧥 DYR").setBold().setFontSize(24).setFontColor(redColor))
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setPadding(10));
        
        headerTable.addCell(new Cell()
            .add(new Paragraph("DYR - CHAQUETAS DE CUERO SINTÉTICO")
                .setBold().setFontSize(16).setFontColor(darkGray))
            .add(new Paragraph("Bogotá, Colombia | ")
                .setFontSize(10).setFontColor(darkGray))
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setPadding(5));
        
        headerTable.addCell(new Cell()
            .add(new Paragraph("FECHA:")
                .setBold().setFontSize(9).setFontColor(darkGray))
            .add(new Paragraph(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFontSize(10).setFontColor(darkGray))
            .add(new Paragraph("N° " + System.currentTimeMillis() % 100000)
                .setBold().setFontSize(11).setFontColor(redColor))
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            .setPadding(5));
        
        document.add(headerTable);
        document.add(new Paragraph(" ").setMarginBottom(10));
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE DE INVENTARIO")
            .setBold()
            .setFontSize(20)
            .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
            .setBackgroundColor(darkRed)
            .setTextAlignment(TextAlignment.CENTER)
            .setPaddingTop(12)
            .setPaddingBottom(12)
            .setMarginBottom(15);
        document.add(titulo);
        
        // Resumen ejecutivo
        int totalStock = inventarios.stream().mapToInt(Inventario::getStockActual).sum();
        
        float[] summaryWidths = {300f, 300f};
        Table summaryTable = new Table(summaryWidths);
        summaryTable.setWidth(UnitValue.createPercentValue(100));
        summaryTable.setMarginBottom(20);
        
        summaryTable.addCell(new Cell()
            .add(new Paragraph("TOTAL PRODUCTOS").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(inventarios.size())).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(15));
        
        summaryTable.addCell(new Cell()
            .add(new Paragraph("STOCK TOTAL").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(totalStock)).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(15));
        
        document.add(summaryTable);
        
        // Tabla de datos
        float[] columnWidths = {60f, 300f, 100f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"ID", "Producto", "Stock"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                .add(new Paragraph(header).setBold().setFontSize(11).setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                .setBackgroundColor(darkRed)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddingTop(10)
                .setPaddingBottom(10));
        }
        
        // Datos con filas alternadas
        int rowIndex = 0;
        for (var i : inventarios) {
            DeviceRgb rowColor = (rowIndex % 2 == 0) ? new DeviceRgb(255, 255, 255) : lightGray;
            
            table.addCell(new Cell()
                .add(new Paragraph(i.getId().toString()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(i.getProducto().getNombre()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(i.getStockActual().toString()).setFontSize(9).setBold().setFontColor(redColor))
                .setBackgroundColor(rowColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));
            
            rowIndex++;
        }
        
        document.add(table);
        
        // Footer
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("DYR - Chaquetas de Cuero Sintético © 2024 | www.dyr.com")
            .setFontSize(8)
            .setFontColor(darkGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20);
        document.add(footer);
        document.close();
    }

    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam(required = false) String producto,
                            @RequestParam(required = false) Integer stockMin,
                            @RequestParam(required = false) Integer stockMax,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var inventarios = inventarioService.listarInventarios().stream()
                .filter(i -> producto == null || producto.isBlank() || i.getProducto().getNombre().toLowerCase().contains(producto.toLowerCase()))
                .filter(i -> stockMin == null || i.getStockActual() >= stockMin)
                .filter(i -> stockMax == null || i.getStockActual() <= stockMax)
                .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario");
        
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"ID", "Producto", "Stock"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (var inv : inventarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(inv.getId());
            row.createCell(1).setCellValue(inv.getProducto().getNombre());
            row.createCell(2).setCellValue(inv.getStockActual());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
