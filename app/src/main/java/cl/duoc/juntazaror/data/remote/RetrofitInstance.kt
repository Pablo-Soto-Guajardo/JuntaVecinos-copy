package cl.duoc.juntazaror.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Guía 14: instancia única de Retrofit (object = singleton).
 * "by lazy" crea el cliente solo la primera vez que se usa.
 */
object RetrofitInstance {

    /**
     * Dirección del api-gateway (docker compose, puerto 9090).
     * - Emulador de Android Studio: 10.0.2.2 apunta al PC donde corre Docker.
     * - Celular por USB: ejecutar "adb reverse tcp:9090 tcp:9090" y usar "http://127.0.0.1:9090/".
     * - Celular por Wi-Fi: usar la IPv4 del PC (comando ipconfig), ej: "http://192.168.1.15:9090/".
     */
    const val BASE_URL = "http://127.0.0.1:9090/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> objetos Kotlin
            .build()
            .create(ApiService::class.java)
    }
}
