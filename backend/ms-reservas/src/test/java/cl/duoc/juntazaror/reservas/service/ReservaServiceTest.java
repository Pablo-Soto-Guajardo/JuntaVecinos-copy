package cl.duoc.juntazaror.reservas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.juntazaror.reservas.dto.ReservaDTO;
import cl.duoc.juntazaror.reservas.exception.BadRequestException;
import cl.duoc.juntazaror.reservas.exception.ConflictException;
import cl.duoc.juntazaror.reservas.exception.ResourceNotFoundException;
import cl.duoc.juntazaror.reservas.model.Reserva;
import cl.duoc.juntazaror.reservas.repository.ReservaRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class ReservaServiceTest {

    private ReservaService reservaService;

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private final LocalDate enUnaSemana = LocalDate.now().plusDays(7);

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(reservaRepository, webClient, "/usuarios/%d/exists");
    }

    /** Simula la respuesta de ms-admin: GET /usuarios/{id}/exists */
    private void mockUsuarioExiste(boolean existe) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(existe));
    }

    private ReservaDTO nuevaSolicitud(String dependencia, int asistentes, LocalDate fecha) {
        return new ReservaDTO(null, 1L, "Camila Rojas", dependencia, fecha.toString(), "TARDE",
                "Cumpleaños familiar de prueba", asistentes, null, null);
    }

    @Test
    void crearReservaDisponibleQuedaPendienteConTarifa() {
        mockUsuarioExiste(true);
        when(reservaRepository.existsByDependenciaAndFechaAndBloqueAndEstadoNot(
                "SALON_PRINCIPAL", enUnaSemana, "TARDE", "RECHAZADA")).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(5L);
            return r;
        });

        Reserva creada = reservaService.crear(nuevaSolicitud("SALON_PRINCIPAL", 40, enUnaSemana), "Bearer abc");

        assertEquals(5L, creada.getId());
        assertEquals("PENDIENTE", creada.getEstado());
        assertEquals(35000L, creada.getMonto()); // tarifa del salón principal
        verify(requestHeadersSpec).header("Authorization", "Bearer abc"); // reenvía el token
    }

    @Test
    void crearEnBloqueOcupadoLanzaConflicto() {
        when(reservaRepository.existsByDependenciaAndFechaAndBloqueAndEstadoNot(
                "SALON_PRINCIPAL", enUnaSemana, "TARDE", "RECHAZADA")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> reservaService.crear(nuevaSolicitud("SALON_PRINCIPAL", 40, enUnaSemana), "Bearer abc"));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearSuperandoCapacidadLanzaError() {
        // La sala de reuniones tiene capacidad 20
        BadRequestException error = assertThrows(BadRequestException.class,
                () -> reservaService.crear(nuevaSolicitud("SALA_REUNIONES", 25, enUnaSemana), "Bearer abc"));
        assertTrue(error.getMessage().contains("20"));
    }

    @Test
    void crearEnFechaPasadaLanzaError() {
        assertThrows(BadRequestException.class, () -> reservaService
                .crear(nuevaSolicitud("MULTICANCHA", 10, LocalDate.now().minusDays(1)), "Bearer abc"));
    }

    @Test
    void crearConDependenciaInventadaLanzaError() {
        assertThrows(BadRequestException.class,
                () -> reservaService.crear(nuevaSolicitud("PISCINA", 10, enUnaSemana), "Bearer abc"));
    }

    @Test
    void crearConUsuarioInexistenteLanzaNoEncontrado() {
        mockUsuarioExiste(false);
        when(reservaRepository.existsByDependenciaAndFechaAndBloqueAndEstadoNot(
                anyString(), any(LocalDate.class), anyString(), anyString())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> reservaService.crear(nuevaSolicitud("MULTICANCHA", 10, enUnaSemana), "Bearer abc"));
    }

    @Test
    void confirmarReserva() {
        Reserva pendiente = new Reserva(1L, 1L, "Camila Rojas", "MULTICANCHA", enUnaSemana, "NOCHE",
                "Partido de baby fútbol", 12, 18000L, "PENDIENTE");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = reservaService.cambiarEstado(1L, "CONFIRMADA");

        assertEquals("CONFIRMADA", resultado.getEstado());
    }

    @Test
    void cambiarAEstadoInvalidoLanzaError() {
        assertThrows(BadRequestException.class, () -> reservaService.cambiarEstado(1L, "PAGADA"));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void noSePuedeCancelarUnaReservaConfirmada() {
        Reserva confirmada = new Reserva(2L, 1L, "Camila Rojas", "SALON_PRINCIPAL", enUnaSemana, "TARDE",
                "Cumpleaños familiar", 40, 35000L, "CONFIRMADA");
        when(reservaRepository.findById(2L)).thenReturn(Optional.of(confirmada));

        assertThrows(BadRequestException.class, () -> reservaService.eliminar(2L));
        verify(reservaRepository, never()).deleteById(eq(2L));
    }

    @Test
    void cancelarReservaPendiente() {
        Reserva pendiente = new Reserva(3L, 1L, "Camila Rojas", "SALA_REUNIONES", enUnaSemana, "MANANA",
                "Reunión de vecinos", 10, 12000L, "PENDIENTE");
        when(reservaRepository.findById(3L)).thenReturn(Optional.of(pendiente));

        reservaService.eliminar(3L);

        verify(reservaRepository).deleteById(3L);
    }

    @Test
    void listarDevuelveLasReservasDelRepositorio() {
        when(reservaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of(
                new Reserva(1L, 1L, "Camila Rojas", "MULTICANCHA", enUnaSemana, "NOCHE", "Partido", 12, 18000L, "PENDIENTE")));

        assertEquals(1, reservaService.listar().size());
    }
}
