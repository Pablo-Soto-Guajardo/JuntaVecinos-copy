package cl.duoc.juntazaror.finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.juntazaror.finanzas.model.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Los más recientes primero
    List<Movimiento> findAllByOrderByFechaDescIdDesc();
}
