package cl.duoc.juntazaror.finanzas;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.juntazaror.finanzas.model.Movimiento;
import cl.duoc.juntazaror.finanzas.repository.MovimientoRepository;

/** Movimientos de ejemplo la primera vez (montos FICTICIOS). */
@Component
public class DataLoader implements CommandLineRunner {

    private final MovimientoRepository movimientoRepository;

    DataLoader(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public void run(String... args) {
        if (movimientoRepository.count() > 0) return;
        LocalDate hoy = LocalDate.now();
        String tesorera = "Patricia Soto";
        movimientoRepository.save(new Movimiento(null, "INGRESO", "CUOTA_SOCIAL", 120000L, hoy.minusDays(20),
                "Cuotas sociales del mes (ficticio)", null, tesorera));
        movimientoRepository.save(new Movimiento(null, "INGRESO", "ARRIENDO", 35000L, hoy.minusDays(12),
                "Arriendo salón principal - evento vecinal", null, tesorera));
        movimientoRepository.save(new Movimiento(null, "GASTO", "LUZ", 42500L, hoy.minusDays(8),
                "Boleta de luz de la sede", null, tesorera));
        movimientoRepository.save(new Movimiento(null, "GASTO", "AGUA", 18900L, hoy.minusDays(3),
                "Boleta de agua de la sede", null, tesorera));
    }
}
