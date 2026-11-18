package cl.duoc.juntazaror.data.repository

import cl.duoc.juntazaror.data.local.dao.MovimientoDao
import cl.duoc.juntazaror.data.model.Movimiento
import cl.duoc.juntazaror.data.model.ResumenFinanciero
import cl.duoc.juntazaror.data.model.TipoMovimiento
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.RetrofitInstance
import cl.duoc.juntazaror.data.remote.dto.aDto
import cl.duoc.juntazaror.data.remote.dto.aMovimiento

/**
 * Repositorio de movimientos financieros.
 * El servidor (ms-finanzas) es la fuente oficial; Room guarda la copia local.
 */
class MovimientoRepository(
    private val movimientoDao: MovimientoDao,
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun obtenerLocales(): List<Movimiento> = movimientoDao.obtenerTodos()

    /** GET al servidor y reemplaza la copia local. Lanza excepción si no hay conexión. */
    suspend fun sincronizar(token: String): List<Movimiento> {
        val remotos = api.getMovimientos(token).map { it.aMovimiento() }
        movimientoDao.borrarTodos()
        movimientoDao.insertarTodos(remotos)
        return remotos
    }

    /** POST al servidor; si lo acepta, se guarda también en Room. */
    suspend fun registrar(movimiento: Movimiento, token: String): Movimiento {
        val guardado = api.registrarMovimiento(token, movimiento.aDto()).aMovimiento()
        movimientoDao.insertar(guardado)
        return guardado
    }

    fun calcularResumen(movimientos: List<Movimiento>): ResumenFinanciero =
        ResumenFinanciero(
            totalIngresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto },
            totalGastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }
        )
}
