package cl.duoc.juntazaror.reservas;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.juntazaror.reservas.model.Dependencia;
import cl.duoc.juntazaror.reservas.model.Reserva;
import cl.duoc.juntazaror.reservas.repository.ReservaRepository;

/** Carga 2 reservas de ejemplo de la vecina Camila Rojas (usuario id 1). Datos FICTICIOS. */
@Component
public class DataLoader implements CommandLineRunner {

    private final ReservaRepository reservaRepository;

    DataLoader(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void run(String... args) {
        if (reservaRepository.count() > 0) return;
        LocalDate hoy = LocalDate.now();
        reservaRepository.save(new Reserva(null, 1L, "Camila Rojas", Dependencia.SALON_PRINCIPAL.name(),
                hoy.plusDays(5), "TARDE", "Cumpleaños familiar con 40 invitados", 40,
                Dependencia.SALON_PRINCIPAL.getTarifaPorBloque(), "CONFIRMADA"));
        reservaRepository.save(new Reserva(null, 1L, "Camila Rojas", Dependencia.SALA_REUNIONES.name(),
                hoy.plusDays(9), "NOCHE", "Reunión del club de adulto mayor", 15,
                Dependencia.SALA_REUNIONES.getTarifaPorBloque(), "PENDIENTE"));
    }
}
