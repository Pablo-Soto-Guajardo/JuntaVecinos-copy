package cl.duoc.juntazaror.reservas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.juntazaror.reservas.dto.ReservaDTO;
import cl.duoc.juntazaror.reservas.exception.ConflictException;
import cl.duoc.juntazaror.reservas.exception.GlobalExceptionHandler;
import cl.duoc.juntazaror.reservas.model.Reserva;
import cl.duoc.juntazaror.reservas.service.ReservaService;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    private final LocalDate fecha = LocalDate.now().plusDays(7);
    private Reserva reserva;
    private String json;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservaController(reservaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        reserva = new Reserva(1L, 1L, "Camila Rojas", "SALON_PRINCIPAL", fecha, "TARDE",
                "Cumpleaños familiar", 40, 35000L, "PENDIENTE");
        json = "{\"usuarioId\":1,\"nombreSolicitante\":\"Camila Rojas\",\"dependencia\":\"SALON_PRINCIPAL\","
                + "\"fecha\":\"" + fecha + "\",\"bloque\":\"TARDE\",\"motivo\":\"Cumpleaños familiar\",\"asistentes\":40}";
    }

    @Test
    void listarReservas() throws Exception {
        when(reservaService.listar()).thenReturn(List.of(reserva));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].dependencia").value("SALON_PRINCIPAL"))
                .andExpect(jsonPath("$[0].fecha").value(fecha.toString()))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void listarDependencias() throws Exception {
        mockMvc.perform(get("/reservas/dependencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].codigo").value("SALON_PRINCIPAL"))
                .andExpect(jsonPath("$[0].capacidad").value(80));
    }

    @Test
    void crearReserva() throws Exception {
        when(reservaService.crear(any(ReservaDTO.class), anyString())).thenReturn(reserva);

        mockMvc.perform(post("/reservas")
                        .header("Authorization", "Bearer abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.monto").value(35000));
    }

    @Test
    void crearEnBloqueOcupadoResponde409() throws Exception {
        when(reservaService.crear(any(ReservaDTO.class), anyString()))
                .thenThrow(new ConflictException("Este bloque ya está reservado, elige otro"));

        mockMvc.perform(post("/reservas")
                        .header("Authorization", "Bearer abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este bloque ya está reservado, elige otro"));
    }

    @Test
    void cambiarEstado() throws Exception {
        reserva.setEstado("CONFIRMADA");
        when(reservaService.cambiarEstado(1L, "CONFIRMADA")).thenReturn(reserva);

        mockMvc.perform(put("/reservas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CONFIRMADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void eliminarReserva() throws Exception {
        doNothing().when(reservaService).eliminar(1L);

        mockMvc.perform(delete("/reservas/1"))
                .andExpect(status().isNoContent());
        verify(reservaService, times(1)).eliminar(1L);
    }
}
