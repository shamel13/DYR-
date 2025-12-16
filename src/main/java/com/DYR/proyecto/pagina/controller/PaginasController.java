package com.DYR.proyecto.pagina.controller;

// DESHABILITADO - Usa PaginaController en su lugar
/*
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class PaginasController {

    private String readFile(String filename) {
        try {
            String path = "src/main/resources/templates/" + filename + ".html";
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "<h1>Error: Página no encontrada - " + filename + "</h1>";
        }
    }

    @GetMapping({"/", "/index", "/pagina", "/pagina/"})
    @ResponseBody
    public String index() {
        return readFile("pagina/index");
    }

    @GetMapping("/pagina/index")
    @ResponseBody
    public String paginaIndex() {
        return readFile("pagina/index");
    }

    @GetMapping("/pagina/catalogo")
    @ResponseBody
    public String catalogo() {
        return readFile("pagina/catalogo");
    }

    @GetMapping("/pagina/acerca")
    @ResponseBody
    public String acerca() {
        return readFile("pagina/acerca");
    }

    @GetMapping("/pagina/contacto")
    @ResponseBody
    public String contacto() {
        return readFile("pagina/contacto");
    }
}
*/
