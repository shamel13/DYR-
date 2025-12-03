package com.DYR.proyecto.pedido.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.DYR.proyecto.cliente.model.Cliente;
import com.DYR.proyecto.auth.model.Usuario;
import com.DYR.proyecto.producto.model.Producto;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="cliente_id")
    private Cliente cliente;

    @ManyToOne @JoinColumn(name="user_id")
    private Usuario user;

    @ManyToOne @JoinColumn(name="producto_id")
    private Producto producto;

    private Double valorTotal;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUser() { return user; }
    public void setUser(Usuario user) { this.user = user; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
