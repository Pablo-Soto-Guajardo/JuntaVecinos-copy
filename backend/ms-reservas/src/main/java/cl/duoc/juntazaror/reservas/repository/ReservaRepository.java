package cl.duoc.juntazaror.reservas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.juntazaror.reservas.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByOrderByFechaAscIdAsc();

    // true si ya existe una reserva activa (no rechazada) en esa dependencia, fecha y bloque
    boolean existsByDependenciaAndFechaAndBloqueAndEstadoNot(String dependencia, LocalDate fecha,
            String bloque, String estado);
}
