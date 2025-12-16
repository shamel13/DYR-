package com.DYR.proyecto.cliente.controller;

import com.DYR.proyecto.auth.service.UsuarioService;
import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.cliente.service.ClienteService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteMvcController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public ClienteMvcController(ClienteService clienteService, UsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) String telefono,
                        Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        var usuarios = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                .filter(u -> nombre == null || nombre.isBlank() || (u.getName() != null && u.getName().toLowerCase().contains(nombre.toLowerCase())))
                .filter(u -> email == null || email.isBlank() || (u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase())))
                .filter(u -> telefono == null || telefono.isBlank() || (u.getPhone() != null && u.getPhone().contains(telefono)))
                .collect(Collectors.toList());
        model.addAttribute("usuarios", usuarios);
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
        var usuario = usuarioService.buscarUsuario(id);
        model.addAttribute("usuario", usuario);
        return "formularios/clientes/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var usuario = usuarioService.buscarUsuario(id);
        model.addAttribute("usuario", usuario);
        return "formularios/clientes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String name, 
                        @RequestParam String email, @RequestParam String phone,
                        @RequestParam(required = false) String address,
                        @RequestParam(required = false) String city) {
        usuarioService.actualizarUsuario(id, name, email, phone, address, city);
        return "redirect:/clientes?success=Cliente actualizado correctamente";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return "redirect:/clientes?success=Cliente desactivado correctamente";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id) {
        usuarioService.activarUsuario(id);
        return "redirect:/clientes?success=Cliente activado correctamente";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) String nombre,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String telefono,
                          jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var usuarios = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                .filter(u -> nombre == null || nombre.isBlank() || (u.getName() != null && u.getName().toLowerCase().contains(nombre.toLowerCase())))
                .filter(u -> email == null || email.isBlank() || (u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase())))
                .filter(u -> telefono == null || telefono.isBlank() || (u.getPhone() != null && u.getPhone().contains(telefono)))
                .collect(Collectors.toList());
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_reporte.pdf");
        
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
            .add(new Paragraph(" Bogotá, Colombia | ")
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
        Paragraph titulo = new Paragraph("REPORTE DE CLIENTES")
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
        float[] summaryWidths = {600f};
        Table summaryTable = new Table(summaryWidths);
        summaryTable.setWidth(UnitValue.createPercentValue(100));
        summaryTable.setMarginBottom(20);
        
        summaryTable.addCell(new Cell()
            .add(new Paragraph("TOTAL CLIENTES").setBold().setFontSize(10).setFontColor(darkGray).setMarginBottom(5))
            .add(new Paragraph(String.valueOf(usuarios.size())).setBold().setFontSize(24).setFontColor(redColor))
            .setBackgroundColor(lightGray)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(15));
        
        document.add(summaryTable);
        
        // Tabla de datos
        float[] columnWidths = {120f, 120f, 180f, 120f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"Usuario", "Nombre", "Email", "Teléfono"};
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
        for (var u : usuarios) {
            DeviceRgb rowColor = (rowIndex % 2 == 0) ? new DeviceRgb(255, 255, 255) : lightGray;
            
            table.addCell(new Cell()
                .add(new Paragraph(u.getUsername()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(u.getName()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(u.getEmail()).setFontSize(9))
                .setBackgroundColor(rowColor)
                .setPadding(8));
            
            table.addCell(new Cell()
                .add(new Paragraph(u.getPhone() != null ? u.getPhone() : "N/A").setFontSize(9))
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
    public void exportExcel(@RequestParam(required = false) String nombre,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String telefono,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        var usuarios = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                .filter(u -> nombre == null || nombre.isBlank() || (u.getName() != null && u.getName().toLowerCase().contains(nombre.toLowerCase())))
                .filter(u -> email == null || email.isBlank() || (u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase())))
                .filter(u -> telefono == null || telefono.isBlank() || (u.getPhone() != null && u.getPhone().contains(telefono)))
                .collect(Collectors.toList());
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_reporte.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");
        
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        String[] headers = {"Usuario", "Nombre", "Email", "Teléfono"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (var u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getUsername());
            row.createCell(1).setCellValue(u.getName());
            row.createCell(2).setCellValue(u.getEmail());
            row.createCell(3).setCellValue(u.getPhone() != null ? u.getPhone() : "N/A");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
