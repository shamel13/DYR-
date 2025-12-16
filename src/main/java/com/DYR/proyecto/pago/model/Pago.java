package com.DYR.proyecto.pago.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.DYR.proyecto.auth.model.Usuario;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String metodoPago; // Tarjeta, PayPal, Transferencia, Efectivo

    private Double monto;

    private String estado; // Pendiente, Procesando, Completado, Fallido, Reembolsado

    private String numeroReferencia;

    // Para tarjetas
    private String numeroTarjeta; // Solo últimos 4 dígitos

    private String nombreTitular;

    private String tipoTarjeta; // Visa, Mastercard, etc.

    // Para PayPal
    private String emailPaypal;

    private String transactionIdPaypal;

    // Para transferencia
    private String bancoCuenta;

    private String numeroTransferencia;

    // Información de contacto y envío
    private String telefono;
    private String emailContacto;
    private String direccion;
    private String ciudad;
    private String codigoPostal;

    private String descripcion;

    private LocalDateTime fechaPago = LocalDateTime.now();

    private LocalDateTime fechaActualizacion;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    public String getTipoTarjeta() { return tipoTarjeta; }
    public void setTipoTarjeta(String tipoTarjeta) { this.tipoTarjeta = tipoTarjeta; }

    public String getEmailPaypal() { return emailPaypal; }
    public void setEmailPaypal(String emailPaypal) { this.emailPaypal = emailPaypal; }

    public String getTransactionIdPaypal() { return transactionIdPaypal; }
    public void setTransactionIdPaypal(String transactionIdPaypal) { this.transactionIdPaypal = transactionIdPaypal; }

    public String getBancoCuenta() { return bancoCuenta; }
    public void setBancoCuenta(String bancoCuenta) { this.bancoCuenta = bancoCuenta; }

    public String getNumeroTransferencia() { return numeroTransferencia; }
    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
