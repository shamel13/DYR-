package com.DYR.proyecto.pagina.controller;

import com.DYR.proyecto.inventario.service.InventarioService;
import com.DYR.proyecto.pagina.dto.ProductoCatalogoView;
import com.DYR.proyecto.producto.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class PaginaController {

    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private static final Logger LOGGER = Logger.getLogger(PaginaController.class.getName());

    public PaginaController(ProductoService productoService, InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @GetMapping({"", "/", "/pagina", "/pagina/"})
    public String indexRoot(Model model, HttpSession session) {
        Object name = session.getAttribute("currentUserName");
        if (name != null) model.addAttribute("currentUserName", name.toString());
        return "pagina/index";
    }

    @GetMapping("/pagina/index")
    public String index(Model model, HttpSession session) {
        Object name = session.getAttribute("currentUserName");
        if (name != null) model.addAttribute("currentUserName", name.toString());
        return "pagina/index";
    }

    @GetMapping("/pagina/catalogo")
    public String catalogo(Model model, HttpSession session) {
        try {
            Object name = session.getAttribute("currentUserName");
            if (name != null) model.addAttribute("currentUserName", name.toString());

            // Imágenes disponibles en el servidor
            String[] imagenes = {
                "/uploads/productos/19eae8be-53bf-4e6b-96bf-8f06d63d9451.jpg",
                "/uploads/productos/1c57012b-d185-4fd4-9af5-f5d86e9f928f.jpg",
                "/uploads/productos/44a679d9-fbad-451d-8e7a-abe1f3098854.png",
                "/uploads/productos/4eb353d2-2fbc-4124-a33d-e36ba448b713.jpg",
                "/uploads/productos/72868707-87e6-4af8-82b5-ce316774be4d.jpg",
                "/uploads/productos/a07cbda4-d819-459d-a5f0-14bc36f2e573.jpeg"
            };

            List<ProductoCatalogoView> productos = productoService.listarProductos()
                .stream()
                .map(p -> {
                    try {
                        int stock = inventarioService.obtenerStockPorProductoId(p.getId());
                        // Usar imagen real si existe, sino usar una del pool disponible
                        String imagen = (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) 
                            ? p.getImagenUrl() 
                            : imagenes[Math.toIntExact(p.getId() % imagenes.length)];
                        return new ProductoCatalogoView(
                            p.getId(),
                            p.getNombre(),
                            p.getPrecio(),
                            p.getDescripcion(),
                            stock,
                            imagen
                        );
                    } catch (Exception e) {
                        LOGGER.warning("Error al obtener stock para producto " + p.getId() + ": " + e.getMessage());
                        // Usar imagen real si existe, sino usar una del pool disponible
                        String imagen = (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) 
                            ? p.getImagenUrl() 
                            : imagenes[Math.toIntExact(p.getId() % imagenes.length)];
                        return new ProductoCatalogoView(
                            p.getId(),
                            p.getNombre(),
                            p.getPrecio(),
                            p.getDescripcion(),
                            0,
                            imagen
                        );
                    }
                })
                .toList();
            model.addAttribute("productos", productos);
        } catch (Exception e) {
            LOGGER.severe("Error en catálogo: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("productos", Collections.emptyList());
        }
        return "pagina/catalogo";
    }

    @GetMapping("/pagina/contacto")
    public String contacto(Model model, HttpSession session) {
        Object name = session.getAttribute("currentUserName");
        if (name != null) model.addAttribute("currentUserName", name.toString());
        return "pagina/contacto";
    }

    @GetMapping("/pagina/acerca")
    public String acerca(Model model, HttpSession session) {
        Object name = session.getAttribute("currentUserName");
        if (name != null) model.addAttribute("currentUserName", name.toString());
        return "pagina/acerca";
    }
}
