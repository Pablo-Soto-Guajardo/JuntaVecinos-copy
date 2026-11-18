# Junta Zaror

**Integrantes:**
- Pablo Esteban Soto Aranda
- Maximiliano Andrés Mondaca Grandon

---

## 1. Descripción del proyecto

Aplicación Android para la **Junta de Vecinos Población Zaror** (Los Jazmines 425, Cerrillos).
Resuelve el problema del caso: el control de reservas de las dependencias comunitarias es manual
(riesgo de reservas duplicadas) y la información financiera está dispersa, lo que reduce la
transparencia hacia los vecinos.

La app permite:
- **Solicitar y gestionar arriendos** de las dependencias (salón, sala de reuniones, multicancha) **sin duplicidad** de horarios.
- **Registrar ingresos y gastos** con **foto del comprobante** (cámara o galería).
- **Transparentar** el saldo, los ingresos y los gastos a todos los vecinos.
- **Notificar** la confirmación de reservas y el registro de pagos.


## 2. Perfiles de usuario y reglas de negocio

| Perfil | Qué puede hacer |
|---|---|
| **Vecino/a** | Registrarse, solicitar arriendos, ver/cancelar **sus** solicitudes pendientes, consultar las finanzas (solo lectura). |
| **Directiva / Secretaría** | Ver **todas** las reservas, **confirmar o rechazar** solicitudes, registrar movimientos. |
| **Tesorería** | Registrar **ingresos y gastos** con comprobante, consultar reservas. |

Reglas de negocio implementadas:
1. **No se permiten reservas duplicadas**: una dependencia no puede tener dos reservas activas (pendiente o confirmada) en la misma fecha y bloque. Los bloques ocupados aparecen deshabilitados y además **el servidor (ms-reservas) valida la disponibilidad** antes de guardar: si otro vecino tomó el bloque, responde `409 Conflict`.
2. Las reservas solo se pueden pedir desde hoy y con **máximo 90 días** de anticipación.
3. La cantidad de asistentes **no puede superar la capacidad** de la dependencia.
4. Todo **gasto** requiere **foto del comprobante** (respaldo para la rendición de cuentas).
5. Los movimientos **no se pueden eliminar** (trazabilidad) ni tener fecha futura.
6. No se pueden crear dos cuentas con el mismo correo. El RUT se valida con su dígito verificador.

## 3. Funcionalidades implementadas

| Requisito de la pauta | Implementación |
|---|---|
| Interfaz organizada y navegación clara | Material Design 3, `NavHost` + `NavController`, rutas tipo-seguras con `sealed class Screen`, barra inferior (`NavigationBar`) y `NavigationRail` en pantallas anchas, `TopAppBar` en todas las pantallas. |
| Formularios validados con íconos y mensajes | 5 formularios: **Login, Registro, Solicitud de arriendo, Registro de movimiento, Edición de perfil**. Cada campo tiene ícono, borde rojo (`isError`), ícono de error y mensaje (`supportingText`). |
| Validaciones desde la lógica | `utils/Validaciones.kt` (funciones puras, sin Compose) usadas por los ViewModels. |
| Animaciones funcionales | Transiciones entre pantallas, splash con logo animado (`spring`), botón con estado de carga (`AnimatedContent`), mensajes de éxito/error (`AnimatedVisibility`), color de estado animado (`animateColorAsState`), saldo tipo contador (`animateFloatAsState`), tarjetas expandibles (`animateContentSize`), listas animadas (`animateItem`), `Crossfade` en imágenes. |
| Estructura modular + persistencia local | MVVM. **Room (SQLite)** para usuarios, reservas y movimientos. **DataStore Preferences** para la sesión y la preferencia de notificaciones. |
| Recursos nativos (mín. 2) | **Cámara** (`TakePicture` + `FileProvider` + permiso en tiempo de ejecución), **Galería** (`GetContent`) y **Notificaciones locales** (`POST_NOTIFICATIONS` en Android 13+). |
| **Consumo de API REST (Guía 14)** | **Retrofit 2 + Gson**: `data/remote/ApiService.kt` (GET, POST, PUT, DELETE) y `RetrofitInstance.kt` (singleton `by lazy`). Login con **token JWT** guardado en DataStore y enviado en el encabezado `Authorization`. Pantalla `DependenciasScreen` que muestra datos de la API. |
| **Pruebas (Guía 15)** | **Kotest + MockK + JUnit 5** en `app/src/test` (ViewModel, repositorios, validaciones, mapeos, errores) y **pruebas de interfaz Compose** en `app/src/androidTest`. El backend tiene pruebas **JUnit 5 + Mockito + MockMvc**. |
| Adaptabilidad | `WindowSizeClass` (`ui/utils/WindowSizeUtils.kt`): Inicio con diseño compacto/expandido, `NavigationRail`, grilla adaptable de reservas, doble panel en Finanzas (`BoxWithConstraints`), formularios con ancho máximo. |

## 4. Arquitectura y estructura de carpetas

Patrón **MVVM**: la UI (Composables) solo muestra el estado y avisa eventos; el **ViewModel** contiene la
lógica de presentación y expone `StateFlow` (la UI lo lee con `collectAsState`); el **Repository** es la única puerta a los datos: pide la información al **backend con Retrofit** y guarda una copia en **Room** para mostrarla sin conexión.
Cada ViewModel extiende `AndroidViewModel` y carga sus datos con `cargarDatos()` dentro de `viewModelScope.launch`, como en la Guía 12.

```
app/src/main/java/cl/duoc/juntazaror/
├── MainActivity.kt            → Punto de entrada: crea el canal de notificaciones y llama a AppNavigation
├── data/
│   ├── model/                 → Entidades Room (Usuario, Reserva, Movimiento) y estados de formularios
│   ├── local/                 → AppDatabase (Room, copia local), DAOs, Converters, SesionDataStore (sesión + token)
│   ├── remote/                → ApiService (Retrofit), RetrofitInstance, ErroresApi, dto/ (JSON de la API + Mapeos)
│   └── repository/            → UsuarioRepository, ReservaRepository, MovimientoRepository,
│                                DependenciaRepository, SesionRepository
├── viewmodel/                 → MainViewModel (navegación), AuthViewModel, ReservaViewModel,
│                                FinanzasViewModel, PerfilViewModel, DependenciaViewModel
├── navigation/                → Screen (rutas), NavigationEvent, AppNavigation (NavHost)
├── ui/
│   ├── screens/               → Pantallas (login, registro, inicio/, reservas/, finanzas/, perfil)
│   ├── components/            → Componentes reutilizables (CampoTextoValidado, SelectorDesplegable,
│   │                            CampoFecha, BotonCarga, ImagenInteligente, BotonesImagen, tarjetas…)
│   ├── theme/                 → Colores, tipografía y tema Material 3
│   └── utils/                 → WindowSizeUtils (adaptabilidad)
└── utils/                     → Validaciones, Formatos, ImagenUtils, NotificacionHelper

app/src/test/        → Pruebas unitarias (Kotest + MockK)
app/src/androidTest/ → Pruebas de interfaz (Compose + JUnit)

backend/             → Microservicios Spring Boot (ver sección 7)
├── api-gateway/     → Puerto 9090. Único punto de entrada de la app. Valida el token JWT.
├── ms-admin/        → Puerto 9091. Usuarios, roles, login y registro (genera el JWT).
├── ms-reservas/     → Puerto 9092. Reservas y disponibilidad de dependencias.
├── ms-finanzas/     → Puerto 9093. Ingresos, gastos y resumen financiero.
└── docker-compose.yml
```

## 5. Tecnologías

**App:** Kotlin 2.0 · Jetpack Compose · Material Design 3 · Navigation Compose · ViewModel + StateFlow ·
Room (SQLite) + KSP · DataStore Preferences · Retrofit 2 + Gson · Coil · Activity Result API · FileProvider ·
Kotest · MockK · JUnit 5 · Compose UI Test.

**Backend:** Java 21 · Spring Boot · Spring Cloud Gateway · Spring Security (JWT) · Spring Data JPA ·
MySQL · Lombok · Docker Compose · JUnit 5 + Mockito.

## 6. Pasos para ejecutar

> Primero se levanta el **backend** (sección 7) y luego la app.

1. Clonar el repositorio:
   ```bash
   git clone <URL-del-repositorio>
   ```
2. Abrir la carpeta en **Android Studio** (Meerkat o superior) y esperar la sincronización de Gradle.
3. Crear un emulador en **Device Manager** (recomendado: Pixel con Android 13 / API 33 o superior)
   o conectar un teléfono con **Depuración USB** activada.
4. Revisar la dirección del servidor en `data/remote/RetrofitInstance.kt` (`BASE_URL`):
   - Emulador: `http://10.0.2.2:9090/` (viene así).
   - Celular por USB: ejecutar `adb reverse tcp:9090 tcp:9090` y usar `http://127.0.0.1:9090/`.
   - Celular por Wi-Fi: usar la IPv4 del PC (comando `ipconfig`), por ejemplo `http://192.168.1.15:9090/`.
5. Presionar **Run ▶ 'app'**.
6. Ingresar con una cuenta de prueba (las crea `ms-admin` la primera vez; contraseña de todas: **`junta123`**):

| Perfil | Correo |
|---|---|
| Vecino/a | `vecino@zaror.cl` |
| Directiva | `directiva@zaror.cl` |
| Tesorería | `tesoreria@zaror.cl` |

   También se puede crear una cuenta nueva de vecino/a desde **"Regístrate"**.

### Permisos que solicita la app
- **Cámara**: al tocar "Cámara" por primera vez (foto de perfil o comprobante).
- **Notificaciones** (Android 13+): al ingresar por primera vez o al activarlas en Perfil.
- La **galería** usa el selector del sistema y no necesita permiso en tiempo de ejecución.


## 7. Backend (microservicios)

Requisitos: **Docker Desktop** y **MySQL** encendido en el puerto 3306 con usuario `root` sin contraseña (XAMPP o Laragon).
Cada microservicio crea su propia base de datos (`bd_junta_admin`, `bd_junta_reservas`, `bd_junta_finanzas`).

```bat
cd backend
actualizar-maven.bat          :: solo la primera vez (Maven desde el mirror de Google)
build-all.bat                 :: compila los 4 proyectos y genera los .jar
docker compose up -d --build  :: levanta los 4 contenedores
```

Para detenerlos: `docker compose down`.

### Endpoints (todos a través del gateway `http://localhost:9090`)

| Método | Ruta | Token | Descripción |
|---|---|---|---|
| POST | `/auth/login` | No | `{correo, clave}` → `{status, token, usuario}` |
| POST | `/auth/register` | No | Crea un vecino y devuelve su token (409 si el correo existe) |
| PUT | `/usuarios/{id}` | Sí | Edita nombre, teléfono y dirección |
| GET | `/reservas/dependencias` | No | Catálogo de dependencias (capacidad y tarifa) |
| GET | `/reservas` | Sí | Lista de reservas |
| POST | `/reservas` | Sí | Solicita una reserva (409 si el bloque está ocupado) |
| PUT | `/reservas/{id}/estado` | Sí | `{estado: CONFIRMADA o RECHAZADA}` |
| DELETE | `/reservas/{id}` | Sí | Cancela una solicitud pendiente |
| GET | `/movimientos` | Sí | Lista de ingresos y gastos |
| GET | `/movimientos/resumen` | Sí | Total de ingresos, gastos y saldo |
| POST | `/movimientos` | Sí | Registra un ingreso o gasto (los gastos exigen comprobante) |

Las rutas con token necesitan el encabezado `Authorization: Bearer <token>` (se prueba en Postman con el token que entrega `/auth/login`).

## 8. Pruebas

| Dónde | Cómo se ejecutan | Qué prueban |
|---|---|---|
| `app/src/test` | Android Studio: clic derecho en la carpeta `test` → **Run 'Tests in…'**, o `gradlew test` | ViewModel con datos falsos, repositorios con API simulada (MockK), validaciones, conversión de datos y mensajes de error |
| `app/src/androidTest` | Con emulador/celular conectado: clic derecho en `androidTest` → **Run** | Pantalla de dependencias y formulario de login (Compose UI Test) |
| `backend/*/src/test` | `backend\test-all.bat` | Servicios (Mockito) y controladores (MockMvc) de los 3 microservicios |
