package cl.duoc.juntazaror.finanzas.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.duoc.juntazaror.finanzas.dto.MovimientoDTO;
import cl.duoc.juntazaror.finanzas.dto.ResumenDTO;
import cl.duoc.juntazaror.finanzas.exception.BadRequestException;
import cl.duoc.juntazaror.finanzas.exception.ResourceNotFoundException;
import cl.duoc.juntazaror.finanzas.model.CategoriaMovimiento;
import cl.duoc.juntazaror.finanzas.model.Movimiento;
import cl.duoc.juntazaror.finanzas.repository.MovimientoRepository;

/** Reglas de negocio de la tesorería. */
@Service
public class MovimientoService {

    public static final String INGRESO = "INGRESO";
    public static final String GASTO = "GASTO";

    private static final Logger logger = LoggerFactory.getLogger(MovimientoService.class);

    private final MovimientoRepository movimientoRepository;

    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    public List<Movimiento> listar() {
        return movimientoRepository.findAllByOrderByFechaDescIdDesc();
    }

    public Movimiento obtenerPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no existe"));
    }

    public Movimiento registrar(MovimientoDTO dto) {
        logger.info("Registrando {} de {} ({})", dto.getTipo(), dto.getMonto(), dto.getCategoria());

        if (!INGRESO.equals(dto.getTipo()) && !GASTO.equals(dto.getTipo())) {
            throw new BadRequestException("Tipo inválido: use INGRESO o GASTO");
        }
        CategoriaMovimiento categoria = CategoriaMovimiento.desdeCodigo(dto.getCategoria());
        if (!categoria.getTipo().equals(dto.getTipo())) {
            throw new BadRequestException("La categoría " + categoria.name() + " no corresponde a un " + dto.getTipo());
        }
        LocalDate fecha = parsearFecha(dto.getFecha());
        LocalDate hoy = LocalDate.now();
        if (fecha.isAfter(hoy)) {
            throw new BadRequestException("La fecha no puede ser futura");
        }
        if (fecha.isBefore(hoy.minusYears(1))) {
            throw new BadRequestException("No se aceptan movimientos de hace más de un año");
        }
        // Rendición de cuentas: todo gasto debe tener respaldo
        if (GASTO.equals(dto.getTipo()) && (dto.getComprobanteUri() == null || dto.getComprobanteUri().isBlank())) {
            throw new BadRequestException("Los gastos requieren foto del comprobante o boleta");
        }

        Movimiento movimiento = new Movimiento(null, dto.getTipo(), categoria.name(), dto.getMonto(), fecha,
                dto.getDescripcion().trim(), dto.getComprobanteUri(), dto.getRegistradoPor().trim());
        Movimiento guardado = movimientoRepository.save(movimiento);
        logger.info("Movimiento registrado id={}", guardado.getId());
        return guardado;
    }

    /** Suma ingresos y gastos; el saldo es la diferencia. */
    public ResumenDTO calcularResumen(List<Movimiento> movimientos) {
        long ingresos = movimientos.stream().filter(m -> INGRESO.equals(m.getTipo())).mapToLong(Movimiento::getMonto).sum();
        long gastos = movimientos.stream().filter(m -> GASTO.equals(m.getTipo())).mapToLong(Movimiento::getMonto).sum();
        return new ResumenDTO(ingresos, gastos, ingresos - gastos);
    }

    private LocalDate parsearFecha(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new BadRequestException("Formato de fecha inválido, use yyyy-MM-dd");
        }
    }
}
