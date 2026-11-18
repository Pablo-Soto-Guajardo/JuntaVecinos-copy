package cl.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.juntazaror.data.model.Reserva

/**
 * DAO: consultas SQL sobre la tabla "reservas".
 * Esta tabla es una copia local de lo que entrega el microservicio ms-reservas.
 */
@Dao
interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(reserva: Reserva): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(reservas: List<Reserva>)

    @Update
    suspend fun actualizar(reserva: Reserva)

    @Delete
    suspend fun eliminar(reserva: Reserva)

    @Query("SELECT * FROM reservas ORDER BY fechaEpochDay ASC, creadaEn ASC")
    suspend fun obtenerTodas(): List<Reserva>

    @Query("DELETE FROM reservas")
    suspend fun borrarTodas()
}
