package cl.duoc.juntazaror.reservas.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.juntazaror.reservas.dto.ReservaDTO;
import cl.duoc.juntazaror.reservas.exception.BadRequestException;
import cl.duoc.juntazaror.reservas.exception.ConflictException;
import cl.duoc.juntazaror.reservas.exception.ResourceNotFoundException;
import cl.duoc.juntazaror.reservas.model.Dependencia;
import cl.duoc.juntazaror.reservas.model.Reserva;
import cl.duoc.juntazaror.reservas.repository.ReservaRepository;

/**
 * Reglas de negocio del arriendo de dependencias.
 * El SERVIDOR es quien decide si un bloque está disponible: así dos vecinos
 * con distintos teléfonos no pueden reservar el mismo bloque.
 */
@Service
public class ReservaService {

    public static final List<String> BLOQUES = List.of("MANANA", "TARDE", "NOCHE");
    public static final String PENDIENTE = "PENDIENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String RECHAZADA = "RECHAZADA";
    public static final long DIAS_MAXIMOS_ANTICIPACION = 90;

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final WebClient webClient;
    private final String usuarioPath;

    public ReservaService(ReservaRepository reservaRepository, WebClient webClient,
            @Value("${api.usuario.exists}") String usuarioPath) {
        this.reservaRepository = reservaRepository;
        this.webClient = webClient;
        this.usuarioPath = usuarioPath;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAllByOrderByFechaAscIdAsc();
    }

    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no existe"));
    }

    /** Un bloque está disponible si no hay otra reserva activa (no rechazada) en él. */
    public boolean estaDisponible(String dependencia, LocalDate fecha, String bloque) {
        return !reservaRepository.existsByDependenciaAndFechaAndBloqueAndEstadoNot(
                dependencia, fecha, bloque, RECHAZADA);
    }

    public Reserva crear(ReservaDTO dto, String token) {
        logger.info("Creando reserva: dependencia={}, fecha={}, bloque={}", dto.getDependencia(),
                dto.getFecha(), dto.getBloque());

        Dependencia dependencia = Dependencia.desdeCodigo(dto.getDependencia());
        if (!BLOQUES.contains(dto.getBloque())) {
            throw new BadRequestException("Bloque horario inválido");
        }
        LocalDate fecha = parsearFecha(dto.getFecha());
        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) {
            throw new BadRequestException("No se puede reservar en una fecha pasada");
        }
        if (fecha.isAfter(hoy.plusDays(DIAS_MAXIMOS_ANTICIPACION))) {
            throw new BadRequestException("Solo se puede reservar con hasta " + DIAS_MAXIMOS_ANTICIPACION
                    + " días de anticipación");
        }
        if (dto.getAsistentes() > dependencia.getCapacidad()) {
            throw new BadRequestException("Supera la capacidad máxima (" + dependencia.getCapacidad() + " personas)");
        }
        // Regla de negocio principal: no pueden existir dos reservas activas en el mismo bloque
        if (!estaDisponible(dependencia.name(), fecha, dto.getBloque())) {
            throw new ConflictException("Este bloque ya está reservado, elige otro");
        }
        validarUsuario(dto.getUsuarioId(), token);

        Reserva reserva = new Reserva(null, dto.getUsuarioId(), dto.getNombreSolicitante().trim(),
                dependencia.name(), fecha, dto.getBloque(), dto.getMotivo().trim(), dto.getAsistentes(),
                dependencia.getTarifaPorBloque(), PENDIENTE);
        Reserva guardada = reservaRepository.save(reserva);
        logger.info("Reserva creada id={}", guardada.getId());
        return guardada;
    }

    /** Solo se aceptan los estados CONFIRMADA o RECHAZADA (lo hace la directiva). */
    public Reserva cambiarEstado(Long id, String nuevoEstado) {
        if (!CONFIRMADA.equals(nuevoEstado) && !RECHAZADA.equals(nuevoEstado)) {
            throw new BadRequestException("Estado inválido: use CONFIRMADA o RECHAZADA");
        }
        Reserva reserva = obtenerPorId(id);
        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    /** El vecino solo puede cancelar mientras la solicitud está pendiente. */
    public void eliminar(Long id) {
        Reserva reserva = obtenerPorId(id);
        if (!PENDIENTE.equals(reserva.getEstado())) {
            throw new BadRequestException("Solo se pueden cancelar solicitudes pendientes");
        }
        reservaRepository.deleteById(id);
    }

    public LocalDate parsearFecha(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new BadRequestException("Formato de fecha inválido, use yyyy-MM-dd");
        }
    }

    /** Pregunta a ms-admin (a través del gateway) si el solicitante existe. */
    private void validarUsuario(Long usuarioId, String token) {
        Boolean existe = webClient.get()
                .uri(String.format(usuarioPath, usuarioId))
                .header("Authorization", token == null ? "" : token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (existe == null) {
            throw new BadRequestException("No se pudo validar la existencia del usuario");
        }
        if (Boolean.FALSE.equals(existe)) {
            throw new ResourceNotFoundException("El usuario solicitante no existe");
        }
    }
}
