package com.DYR.proyecto.pedido.repository;

import com.DYR.proyecto.pedido.model.Pedido;
import com.DYR.proyecto.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUserOrderByFechaRegistroDesc(Usuario user);
}
