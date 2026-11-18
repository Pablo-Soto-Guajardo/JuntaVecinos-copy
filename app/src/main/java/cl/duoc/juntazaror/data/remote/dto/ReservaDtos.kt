package cl.duoc.juntazaror.data.remote.dto

/**
 * Reserva tal como viaja por la API (ms-reservas).
 * Los enum viajan como texto ("SALON_PRINCIPAL") y la fecha como "yyyy-MM-dd".
 */
data class ReservaDto(
    val id: Long? = null,
    val usuarioId: Long,
    val nombreSolicitante: String,
    val dependencia: String,
    val fecha: String,
    val bloque: String,
    val motivo: String,
    val asistentes: Int,
    val monto: Long? = null,
    val estado: String? = null
)

/** JSON que se envía a PUT /reservas/{id}/estado */
data class EstadoRequest(
    val estado: String
)

/** Catálogo de dependencias (GET /reservas/dependencias). */
data class DependenciaDto(
    val codigo: String,
    val nombre: String,
    val capacidad: Int,
    val tarifaPorBloque: Long
)
