package cl.duoc.juntazaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.juntazaror.data.remote.ErroresApi
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.data.repository.DependenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Guía 14: ViewModel que consume la API REST y expone la lista con StateFlow.
 * La clase y sus miembros son "open" para poder reemplazarlos en las pruebas (Guía 15).
 */
open class DependenciaViewModel(
    private val repository: DependenciaRepository = DependenciaRepository()
) : ViewModel() {

    protected val _dependencias = MutableStateFlow<List<DependenciaDto>>(emptyList())
    open val dependencias: StateFlow<List<DependenciaDto>> = _dependencias

    protected val _cargando = MutableStateFlow(false)
    open val cargando: StateFlow<Boolean> = _cargando

    protected val _error = MutableStateFlow<String?>(null)
    open val error: StateFlow<String?> = _error

    init {
        fetchDependencias()
    }

    /** Llama a la API dentro de una corrutina; si falla, guarda un mensaje de error. */
    open fun fetchDependencias() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _dependencias.value = repository.getDependencias()
            } catch (e: Exception) {
                _error.value = ErroresApi.mensaje(e)
            }
            _cargando.value = false
        }
    }
}
