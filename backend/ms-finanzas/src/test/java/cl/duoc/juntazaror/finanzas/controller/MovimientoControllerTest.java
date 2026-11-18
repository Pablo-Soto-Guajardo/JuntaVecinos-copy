package cl.duoc.juntazaror.finanzas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import cl.duoc.juntazaror.finanzas.dto.MovimientoDTO;
import cl.duoc.juntazaror.finanzas.dto.ResumenDTO;
import cl.duoc.juntazaror.finanzas.exception.BadRequestException;
import cl.duoc.juntazaror.finanzas.exception.GlobalExceptionHandler;
import cl.duoc.juntazaror.finanzas.model.Movimiento;
import cl.duoc.juntazaror.finanzas.service.MovimientoService;

@ExtendWith(MockitoExtension.class)
class MovimientoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovimientoService movimientoService;

    private final LocalDate hoy = LocalDate.now();
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MovimientoController(movimientoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        movimiento = new Movimiento(1L, "INGRESO", "ARRIENDO", 35000L, hoy, "Arriendo salón", null, "Patricia Soto");
    }

    @Test
    void listarMovimientos() throws Exception {
        when(movimientoService.listar()).thenReturn(List.of(movimiento));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].categoria").value("ARRIENDO"))
                .andExpect(jsonPath("$[0].fecha").value(hoy.toString()));
    }

    @Test
    void obtenerResumen() throws Exception {
        when(movimientoService.listar()).thenReturn(List.of(movimiento));
        when(movimientoService.calcularResumen(anyList())).thenReturn(new ResumenDTO(35000L, 0L, 35000L));

        mockMvc.perform(get("/movimientos/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(35000));
    }

    @Test
    void registrarMovimiento() throws Exception {
        when(movimientoService.registrar(any(MovimientoDTO.class))).thenReturn(movimiento);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"INGRESO\",\"categoria\":\"ARRIENDO\",\"monto\":35000,\"fecha\":\"" + hoy
                                + "\",\"descripcion\":\"Arriendo salón\",\"registradoPor\":\"Patricia Soto\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.monto").value(35000));
    }

    @Test
    void gastoSinComprobanteResponde400() throws Exception {
        when(movimientoService.registrar(any(MovimientoDTO.class)))
                .thenThrow(new BadRequestException("Los gastos requieren foto del comprobante o boleta"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"GASTO\",\"categoria\":\"LUZ\",\"monto\":42500,\"fecha\":\"" + hoy
                                + "\",\"descripcion\":\"Boleta de luz\",\"registradoPor\":\"Patricia Soto\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Los gastos requieren foto del comprobante o boleta"));
    }
}
