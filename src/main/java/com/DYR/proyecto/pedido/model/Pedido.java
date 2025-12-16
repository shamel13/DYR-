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

    private Integer cantidad = 1;

    private Double valorTotal;

    private Double descuento = 0.0;

    private String estado = "Pendiente"; // Pendiente, Procesando, Completado, Cancelado

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Información de envío del pedido
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private String shippingPhone;
    private String shippingEmail;

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

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }

    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }

    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public String getShippingEmail() { return shippingEmail; }
    public void setShippingEmail(String shippingEmail) { this.shippingEmail = shippingEmail; }
}
