package com.DYR.proyecto.producto.controller;

import com.DYR.proyecto.producto.model.Producto;
import com.DYR.proyecto.producto.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
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
@RequestMapping("/productos")
public class ProductoMvcController {

    private final ProductoService productoService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/productos/";

    public ProductoMvcController(ProductoService productoService) {
        this.productoService = productoService;
    }

    private String guardarImagen(MultipartFile archivo) throws Exception {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        // Crear directorio si no existe
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generar nombre único
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreArchivo = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
        Files.write(ruta, archivo.getBytes());

        // Retornar ruta relativa para acceso web
        return "/uploads/productos/" + nombreArchivo;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String nombre,
                       @RequestParam(required = false) Double precioMin,
                       @RequestParam(required = false) Double precioMax,
                       Model model) {
        var productos = productoService.listarProductos().stream()
                .filter(p -> nombre == null || nombre.isBlank() || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
                .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
                .collect(Collectors.toList());
        
        model.addAttribute("productos", productos);
        model.addAttribute("producto", new Producto());
        return "formularios/productos/index";
    }

    @PostMapping
    public String store(@ModelAttribute Producto producto, @RequestParam(required = false) MultipartFile imagen) {
        try {
            if (imagen != null && !imagen.isEmpty()) {
                String rutaImagen = guardarImagen(imagen);
                producto.setImagenUrl(rutaImagen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        productoService.guardarProducto(producto);
        return "redirect:/productos?success=Producto registrado correctamente";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarProducto(id));
        return "formularios/productos/show"; // show.html
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarProducto(id));
        return "formularios/productos/edit"; // edit.html
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String nombre,
                         @RequestParam Double precio,
                         @RequestParam(required = false) String descripcion,
                         @RequestParam(required = false) MultipartFile imagen) {
        var producto = productoService.buscarProducto(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        if (descripcion != null) {
            producto.setDescripcion(descripcion);
        }
        try {
            if (imagen != null && !imagen.isEmpty()) {
                String rutaImagen = guardarImagen(imagen);
                producto.setImagenUrl(rutaImagen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        productoService.actualizarProducto(id, producto);
        return "redirect:/productos/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos?success=Producto eliminado correctamente";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String nombre,
                          @RequestParam(required = false) Double precioMin,
                          @RequestParam(required = false) Double precioMax,
                          jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var productos = productoService.listarProductos().stream()
                .filter(p -> nombre == null || nombre.isBlank() || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
                .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
                .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=productos_reporte.pdf");
        
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
            .add(new Paragraph(", Colombia | ")
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
        Paragraph titulo = new Paragraph("REPORTE DE PRODUCTOS")
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
        double precioPromedio = productos.stream().mapToDouble(Producto::getPrecio).average().orElse(0.0);
        
        float[] summaryWidths = {300f, 300f};
        Table summaryTable = new Table(summaryWidths);
        summaryTable.setWidth(UnitValue.createPercentValue(100));
        summaryTable.setMarginBottom(20);
        
        summaryTable.addCell(new Cell()
            .add(new Paragraph("TOTAL PRODUCTOS").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(productos.size())).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(15));
        
        summaryTable.addCell(new Cell()
            .add(new Paragraph("PRECIO PROMEDIO").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.format("$%,.0f", precioPromedio)).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(15));
        
        document.add(summaryTable);
        
        // Tabla de datos
        float[] columnWidths = {60f, 280f, 120f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"ID", "Nombre", "Precio"};
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
        for (var p : productos) {
            DeviceRgb rowColor = (rowIndex % 2 == 0) ? new DeviceRgb(255, 255, 255) : lightGray;
            
            table.addCell(new Cell()
                .add(new Paragraph(p.getId().toString()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(p.getNombre()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(String.format("$%,.0f", p.getPrecio())).setFontSize(9).setBold().setFontColor(redColor))
                .setBackgroundColor(rowColor)
                .setTextAlignment(TextAlignment.RIGHT)
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
    public void exportExcel(@RequestParam(required = false) String nombre,
                            @RequestParam(required = false) Double precioMin,
                            @RequestParam(required = false) Double precioMax,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var productos = productoService.listarProductos().stream()
                .filter(p -> nombre == null || nombre.isBlank() || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
                .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
                .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=productos_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");
        
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"ID", "Nombre", "Precio"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (var p : productos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getPrecio());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
