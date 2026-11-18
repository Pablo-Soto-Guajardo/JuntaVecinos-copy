package cl.duoc.juntazaror.utils

import cl.duoc.juntazaror.data.model.TipoMovimiento
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate

/** Guía 15: pruebas unitarias de las reglas de validación de los formularios. */
class ValidacionesTest : StringSpec({

    "un correo bien escrito es válido" {
        Validaciones.validarCorreo("vecino@zaror.cl") shouldBe null
    }

    "un correo sin @ es inválido" {
        Validaciones.validarCorreo("vecinozaror.cl") shouldNotBe null
    }

    "el RUT con dígito verificador correcto es válido" {
        Validaciones.validarRut("12.345.678-5") shouldBe null
        Validaciones.validarRut("11.111.111-1") shouldBe null
    }

    "el RUT con dígito verificador incorrecto es inválido" {
        Validaciones.validarRut("12.345.678-9") shouldBe "El dígito verificador no corresponde"
    }

    "la contraseña debe tener letras y números" {
        Validaciones.validarClave("abcdef") shouldBe "Debe incluir al menos un número"
        Validaciones.validarClave("junta123") shouldBe null
    }

    "el teléfono debe tener 9 dígitos y comenzar con 9" {
        Validaciones.validarTelefono("912345678") shouldBe null
        Validaciones.validarTelefono("812345678") shouldBe "Debe comenzar con 9"
    }

    "no se puede reservar en una fecha pasada" {
        val hoy = LocalDate.of(2026, 11, 11)
        Validaciones.validarFechaReserva(hoy.minusDays(1), hoy) shouldBe "No puedes reservar en una fecha pasada"
        Validaciones.validarFechaReserva(hoy.plusDays(10), hoy) shouldBe null
    }

    "los asistentes no pueden superar la capacidad de la dependencia" {
        Validaciones.validarAsistentes("25", 20) shouldBe "Supera la capacidad máxima (20 personas)"
        Validaciones.validarAsistentes("15", 20) shouldBe null
    }

    "el monto debe ser mayor a cero" {
        Validaciones.validarMonto("0") shouldBe "El monto debe ser mayor a 0"
        Validaciones.validarMonto("35000") shouldBe null
    }

    "los gastos exigen comprobante, los ingresos no" {
        Validaciones.validarComprobante(TipoMovimiento.GASTO, null) shouldNotBe null
        Validaciones.validarComprobante(TipoMovimiento.INGRESO, null) shouldBe null
    }
})
