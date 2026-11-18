package cl.duoc.juntazaror.remote

import cl.duoc.juntazaror.data.model.BloqueHorario
import cl.duoc.juntazaror.data.model.CategoriaMovimiento
import cl.duoc.juntazaror.data.model.Dependencia
import cl.duoc.juntazaror.data.model.EstadoReserva
import cl.duoc.juntazaror.data.model.Rol
import cl.duoc.juntazaror.data.remote.dto.MovimientoDto
import cl.duoc.juntazaror.data.remote.dto.ReservaDto
import cl.duoc.juntazaror.data.remote.dto.UsuarioDto
import cl.duoc.juntazaror.data.remote.dto.aDto
import cl.duoc.juntazaror.data.remote.dto.aMovimiento
import cl.duoc.juntazaror.data.remote.dto.aReserva
import cl.duoc.juntazaror.data.remote.dto.aUsuario
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

/** Conversión entre el JSON de la API (DTO) y las tablas de Room. */
class MapeosTest : StringSpec({

    "un ReservaDto se convierte en Reserva con enums y fecha epochDay" {
        val dto = ReservaDto(5, 1, "Camila Rojas", "MULTICANCHA", "2026-12-01", "NOCHE",
            "Partido de baby fútbol", 12, 18000, "PENDIENTE")

        val reserva = dto.aReserva()

        reserva.id shouldBe 5L
        reserva.dependencia shouldBe Dependencia.MULTICANCHA
        reserva.bloque shouldBe BloqueHorario.NOCHE
        reserva.estado shouldBe EstadoReserva.PENDIENTE
        reserva.fechaEpochDay shouldBe LocalDate.of(2026, 12, 1).toEpochDay()
    }

    "una Reserva nueva (id 0) se envía al servidor con id nulo y fecha en texto" {
        val dto = ReservaDto(null, 1, "Camila Rojas", "SALA_REUNIONES", "2026-12-15", "MANANA",
            "Reunión de vecinos", 10, 12000, "PENDIENTE").aReserva().aDto()

        dto.id shouldBe null
        dto.fecha shouldBe "2026-12-15"
        dto.dependencia shouldBe "SALA_REUNIONES"
    }

    "un MovimientoDto conserva sus datos al pasar a Room y volver" {
        val original = MovimientoDto(3, "GASTO", "LUZ", 42500, "2026-11-01", "Boleta de luz",
            "content://boleta.jpg", "Patricia Soto")

        val movimiento = original.aMovimiento()

        movimiento.categoria shouldBe CategoriaMovimiento.LUZ
        movimiento.aDto() shouldBe original
    }

    "un UsuarioDto se convierte en Usuario con su rol" {
        val usuario = UsuarioDto(2, "Jorge Muñoz", "12.345.678-5", "directiva@zaror.cl",
            "922222222", "Los Jazmines 425", "DIRECTIVA").aUsuario()

        usuario.rol shouldBe Rol.DIRECTIVA
        usuario.fotoUri shouldBe null
    }
})
