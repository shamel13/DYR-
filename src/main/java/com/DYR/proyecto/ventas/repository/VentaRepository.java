package com.DYR.proyecto.ventas.repository;

import com.DYR.proyecto.ventas.model.Venta;
import com.DYR.proyecto.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUserOrderByFechaVentaDesc(Usuario user);
}
