package com.DYR.proyecto.pago.service;

import com.DYR.proyecto.pago.model.Pago;
import com.DYR.proyecto.pago.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public Pago buscarPago(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago guardarPago(Pago pago) {
        if (pago.getNumeroReferencia() == null || pago.getNumeroReferencia().isEmpty()) {
            pago.setNumeroReferencia("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (pago.getEstado() == null || pago.getEstado().isEmpty()) {
            pago.setEstado("Pendiente");
        }
        return pagoRepository.save(pago);
    }

    public Pago actualizarPago(Long id, Pago pago) {
        pago.setId(id);
        pago.setFechaActualizacion(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }

    public Pago procesarPago(Pago pago) {
        // Simulación de procesamiento
        pago.setEstado("Procesando");
        pagoRepository.save(pago);
        
        // Simular validación (en producción aquí iría la integración con Stripe, PayPal, etc.)
        try {
            Thread.sleep(1000); // Simular latencia de API
            pago.setEstado("Completado");
        } catch (InterruptedException e) {
            pago.setEstado("Fallido");
        }
        
        pago.setFechaActualizacion(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    public Pago reembolsarPago(Long id) {
        Pago pago = buscarPago(id);
        if (pago != null && pago.getEstado().equals("Completado")) {
            pago.setEstado("Reembolsado");
            pago.setFechaActualizacion(LocalDateTime.now());
            return pagoRepository.save(pago);
        }
        return null;
    }
}
