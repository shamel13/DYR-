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
        model.addAttribute("cliente", clienteService.buscarCliente(id));
        model.addAttribute("pedidos", clienteService.listarPedidosPorCliente(id));
        var usuarios = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                .collect(Collectors.toList());
        model.addAttribute("usuarios", usuarios);
        return "formularios/clientes/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarCliente(id));
        return "formularios/clientes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        clienteService.actualizarCliente(id, cliente);
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "redirect:/clientes?success=Cliente eliminado correctamente";
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
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("Reporte de Usuarios").setBold().setFontSize(18));
        document.add(new Paragraph(" "));
        
        Table table = new Table(4);
        table.addHeaderCell(new Cell().add(new Paragraph("Usuario").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Nombre").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Teléfono").setBold()));
        
        for (var u : usuarios) {
            table.addCell(u.getUsername());
            table.addCell(u.getName());
            table.addCell(u.getEmail());
            table.addCell(u.getPhone() != null ? u.getPhone() : "N/A");
        }
        
        document.add(table);
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
