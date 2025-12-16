package com.DYR.proyecto.inventario.repository;

import com.DYR.proyecto.inventario.model.Inventario;
import com.DYR.proyecto.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProducto(Producto producto);
    Optional<Inventario> findByProductoId(Long productoId);
}
