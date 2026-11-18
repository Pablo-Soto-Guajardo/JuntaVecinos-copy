package cl.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.juntazaror.data.model.Movimiento

/**
 * DAO: consultas SQL sobre la tabla "movimientos".
 * Esta tabla es una copia local de lo que entrega el microservicio ms-finanzas.
 */
@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: Movimiento): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(movimientos: List<Movimiento>)

    // Los más recientes primero
    @Query("SELECT * FROM movimientos ORDER BY fechaEpochDay DESC, creadoEn DESC")
    suspend fun obtenerTodos(): List<Movimiento>

    @Query("DELETE FROM movimientos")
    suspend fun borrarTodos()
}
