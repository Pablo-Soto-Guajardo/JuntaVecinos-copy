package cl.duoc.juntazaror.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.juntazaror.finanzas.dto.MovimientoDTO;
import cl.duoc.juntazaror.finanzas.dto.ResumenDTO;
import cl.duoc.juntazaror.finanzas.exception.BadRequestException;
import cl.duoc.juntazaror.finanzas.model.Movimiento;
import cl.duoc.juntazaror.finanzas.repository.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    private MovimientoService movimientoService;
    private final String hoy = LocalDate.now().toString();

    @BeforeEach
    void setUp() {
        movimientoService = new MovimientoService(movimientoRepository);
    }

    @Test
    void registrarIngresoValido() {
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> {
            Movimiento m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });
        MovimientoDTO dto = new MovimientoDTO(null, "INGRESO", "CUOTA_SOCIAL", 50000L, hoy,
                "Cuotas de noviembre", null, "Patricia Soto");

        Movimiento guardado = movimientoService.registrar(dto);

        assertEquals(1L, guardado.getId());
        assertEquals(50000L, guardado.getMonto());
    }

    @Test
    void gastoSinComprobanteLanzaError() {
        MovimientoDTO dto = new MovimientoDTO(null, "GASTO", "LUZ", 42500L, hoy,
                "Boleta de luz", null, "Patricia Soto");

        BadRequestException error = assertThrows(BadRequestException.class, () -> movimientoService.registrar(dto));
        assertTrue(error.getMessage().contains("comprobante"));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void categoriaQueNoCorrespondeAlTipoLanzaError() {
        // LUZ es un gasto, no un ingreso
        MovimientoDTO dto = new MovimientoDTO(null, "INGRESO", "LUZ", 1000L, hoy,
                "Movimiento mal clasificado", null, "Patricia Soto");

        assertThrows(BadRequestException.class, () -> movimientoService.registrar(dto));
    }

    @Test
    void fechaFuturaLanzaError() {
        MovimientoDTO dto = new MovimientoDTO(null, "INGRESO", "ARRIENDO", 35000L,
                LocalDate.now().plusDays(3).toString(), "Arriendo adelantado", null, "Patricia Soto");

        assertThrows(BadRequestException.class, () -> movimientoService.registrar(dto));
    }

    @Test
    void calcularResumenSumaIngresosYGastos() {
        LocalDate fecha = LocalDate.now();
        List<Movimiento> movimientos = List.of(
                new Movimiento(1L, "INGRESO", "CUOTA_SOCIAL", 120000L, fecha, "Cuotas", null, "Patricia"),
                new Movimiento(2L, "INGRESO", "ARRIENDO", 35000L, fecha, "Arriendo", null, "Patricia"),
                new Movimiento(3L, "GASTO", "LUZ", 42500L, fecha, "Luz", "foto", "Patricia"));

        ResumenDTO resumen = movimientoService.calcularResumen(movimientos);

        assertEquals(155000L, resumen.getTotalIngresos());
        assertEquals(42500L, resumen.getTotalGastos());
        assertEquals(112500L, resumen.getSaldo());
    }

    @Test
    void resumenSinMovimientosEsCero() {
        ResumenDTO resumen = movimientoService.calcularResumen(List.of());

        assertEquals(0L, resumen.getSaldo());
    }
}
