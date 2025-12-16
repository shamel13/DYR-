package com.DYR.proyecto.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class AuthPagesController {

    private String readFile(String filename) {
        try {
            String path = "src/main/resources/templates/" + filename + ".html";
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "<h1>Error: Página no encontrada - " + filename + "</h1>";
        }
    }

    @GetMapping("/auth/register-page")
    @ResponseBody
    public String registerPage() {
        return readFile("pagina/register");
    }
}
