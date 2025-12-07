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
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("Reporte de Productos").setBold().setFontSize(18));
        document.add(new Paragraph(" "));
        
        Table table = new Table(3);
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Nombre").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Precio").setBold()));
        
        for (var p : productos) {
            table.addCell(p.getId().toString());
            table.addCell(p.getNombre());
            table.addCell("$" + String.format("%.2f", p.getPrecio()));
        }
        
        document.add(table);
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
