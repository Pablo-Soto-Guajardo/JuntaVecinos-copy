package cl.duoc.juntazaror.data.remote.dto

/** Movimiento financiero tal como viaja por la API (ms-finanzas). */
data class MovimientoDto(
    val id: Long? = null,
    val tipo: String,
    val categoria: String,
    val monto: Long,
    val fecha: String,
    val descripcion: String,
    val comprobanteUri: String? = null,
    val registradoPor: String
)
