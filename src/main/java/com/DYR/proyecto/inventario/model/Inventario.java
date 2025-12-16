package com.DYR.proyecto.inventario.model;

import com.DYR.proyecto.producto.model.Producto;
import com.DYR.proyecto.movimientoInventario.model.MovimientoInventario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoInventario> movimientos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<MovimientoInventario> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoInventario> movimientos) { this.movimientos = movimientos; }
}
