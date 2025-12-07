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
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("Reporte de Inventario").setBold().setFontSize(18));
        document.add(new Paragraph(" "));
        
        Table table = new Table(3);
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Stock").setBold()));
        
        for (var i : inventarios) {
            table.addCell(i.getId().toString());
            table.addCell(i.getProducto().getNombre());
            table.addCell(i.getStockActual().toString());
        }
        
        document.add(table);
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
