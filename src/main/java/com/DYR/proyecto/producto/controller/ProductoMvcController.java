package com.DYR.proyecto.producto.controller;

import com.DYR.proyecto.producto.model.Producto;
import com.DYR.proyecto.producto.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoMvcController {

    private final ProductoService productoService;

    public ProductoMvcController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("producto", new Producto());
        return "formularios/productos/index"; // listado_productos convertido a Thymeleaf
    }

    @PostMapping
    public String store(@ModelAttribute Producto producto) {
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
    public String update(@PathVariable Long id, @ModelAttribute Producto producto) {
        productoService.actualizarProducto(id, producto);
        return "redirect:/productos/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos?success=Producto eliminado correctamente";
    }
}
