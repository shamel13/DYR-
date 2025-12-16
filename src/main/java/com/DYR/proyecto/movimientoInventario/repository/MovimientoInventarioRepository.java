package com.DYR.proyecto.movimientoInventario.repository;

import com.DYR.proyecto.movimientoInventario.model.MovimientoInventario;
import com.DYR.proyecto.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByInventario(Inventario inventario);
}
