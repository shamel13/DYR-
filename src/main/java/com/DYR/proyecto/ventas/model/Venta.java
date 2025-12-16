package com.DYR.proyecto.ventas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.producto.model.Producto;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="cliente_id")
    private Cliente cliente;

    @ManyToOne @JoinColumn(name="user_id")
    private Usuario user;

    @ManyToOne @JoinColumn(name="producto_id")
    private Producto producto;

    private Integer cantidad;

    private Double total;

    private LocalDateTime fechaVenta = LocalDateTime.now();
    
    private String estado = "Completado"; // Completado, Reembolso, Cancelado

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUser() { return user; }
    public void setUser(Usuario user) { this.user = user; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
