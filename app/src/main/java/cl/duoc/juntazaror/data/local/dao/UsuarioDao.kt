package cl.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.juntazaror.data.model.Usuario

/** DAO: define las consultas SQL sobre la tabla "usuarios". */
@Dao
interface UsuarioDao {

    // REPLACE: si el usuario ya estaba guardado, se reemplaza con los datos del servidor
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): Usuario?
}
