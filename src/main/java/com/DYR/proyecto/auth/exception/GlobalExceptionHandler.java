package com.DYR.proyecto.auth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, RedirectAttributes redirectAttributes) {
        // Manejo de errores de duplicados en base de datos
        if (ex.getMessage().contains("email")) {
            redirectAttributes.addFlashAttribute("error", "El email ya está registrado");
        } else if (ex.getMessage().contains("documentNumber")) {
            redirectAttributes.addFlashAttribute("error", "El número de documento ya está registrado");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error: datos duplicados o inválidos");
        }
        return "redirect:/auth/register";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", "Error del servidor. Por favor, inténtalo de nuevo.");
        return "pagina/register";
    }
}
