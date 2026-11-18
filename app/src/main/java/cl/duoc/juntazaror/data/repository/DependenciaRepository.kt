package cl.duoc.juntazaror.data.repository

import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.RetrofitInstance
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto

/**
 * Guía 14: repositorio que obtiene el catálogo de dependencias desde la API.
 * Es "open" para poder crear versiones de prueba en los tests (Guía 15).
 */
open class DependenciaRepository(
    private val api: ApiService = RetrofitInstance.api
) {
    open suspend fun getDependencias(): List<DependenciaDto> = api.getDependencias()
}
