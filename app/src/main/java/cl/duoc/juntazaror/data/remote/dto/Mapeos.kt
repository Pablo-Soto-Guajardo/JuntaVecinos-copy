package cl.duoc.juntazaror.data.remote.dto

import cl.duoc.juntazaror.data.model.BloqueHorario
import cl.duoc.juntazaror.data.model.CategoriaMovimiento
import cl.duoc.juntazaror.data.model.Dependencia
import cl.duoc.juntazaror.data.model.EstadoReserva
import cl.duoc.juntazaror.data.model.Movimiento
import cl.duoc.juntazaror.data.model.Reserva
import cl.duoc.juntazaror.data.model.Rol
import cl.duoc.juntazaror.data.model.TipoMovimiento
import cl.duoc.juntazaror.data.model.Usuario
import java.time.LocalDate

/*
 * Conversión entre los objetos de la API (DTO) y las entidades de Room.
 * Room guarda fechas como epochDay y enums; la API usa texto.
 */

fun UsuarioDto.aUsuario(fotoUri: String? = null): Usuario = Usuario(
    id = id,
    nombre = nombre,
    rut = rut,
    correo = correo,
    telefono = telefono,
    direccion = direccion,
    rol = Rol.valueOf(rol),
    fotoUri = fotoUri
)

fun ReservaDto.aReserva(): Reserva = Reserva(
    id = id ?: 0,
    usuarioId = usuarioId,
    nombreSolicitante = nombreSolicitante,
    dependencia = Dependencia.valueOf(dependencia),
    fechaEpochDay = LocalDate.parse(fecha).toEpochDay(),
    bloque = BloqueHorario.valueOf(bloque),
    motivo = motivo,
    asistentes = asistentes,
    monto = monto ?: 0,
    estado = EstadoReserva.valueOf(estado ?: EstadoReserva.PENDIENTE.name)
)

/** Al crear una reserva el id lo asigna el servidor, por eso se envía null. */
fun Reserva.aDto(): ReservaDto = ReservaDto(
    id = if (id == 0L) null else id,
    usuarioId = usuarioId,
    nombreSolicitante = nombreSolicitante,
    dependencia = dependencia.name,
    fecha = LocalDate.ofEpochDay(fechaEpochDay).toString(),
    bloque = bloque.name,
    motivo = motivo,
    asistentes = asistentes,
    monto = monto,
    estado = estado.name
)

fun MovimientoDto.aMovimiento(): Movimiento = Movimiento(
    id = id ?: 0,
    tipo = TipoMovimiento.valueOf(tipo),
    categoria = CategoriaMovimiento.valueOf(categoria),
    monto = monto,
    fechaEpochDay = LocalDate.parse(fecha).toEpochDay(),
    descripcion = descripcion,
    comprobanteUri = comprobanteUri,
    registradoPor = registradoPor
)

fun Movimiento.aDto(): MovimientoDto = MovimientoDto(
    id = if (id == 0L) null else id,
    tipo = tipo.name,
    categoria = categoria.name,
    monto = monto,
    fecha = LocalDate.ofEpochDay(fechaEpochDay).toString(),
    descripcion = descripcion,
    comprobanteUri = comprobanteUri,
    registradoPor = registradoPor
)
