package cl.duoc.juntazaror.admin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.juntazaror.admin.dto.ActualizarUsuarioRequest;
import cl.duoc.juntazaror.admin.dto.LoginResponse;
import cl.duoc.juntazaror.admin.dto.RegistroRequest;
import cl.duoc.juntazaror.admin.exception.ConflictException;
import cl.duoc.juntazaror.admin.exception.ResourceNotFoundException;
import cl.duoc.juntazaror.admin.model.Usuario;
import cl.duoc.juntazaror.admin.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final HashService hashService = new HashService();
    private final JwtService jwtService = new JwtService("esta_es_mi_compleja_clave_secreta");
    private UsuarioService usuarioService;

    private Usuario vecina;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, jwtService, hashService);
        vecina = new Usuario(1L, "Camila Rojas", "11.111.111-1", "vecino@zaror.cl", "911111111",
                "Los Aromos 120", hashService.sha1("junta123"), "VECINO");
    }

    @Test
    void loginCorrectoEntregaTokenYUsuario() {
        when(usuarioRepository.findByCorreo("vecino@zaror.cl")).thenReturn(vecina);

        LoginResponse respuesta = usuarioService.login("  Vecino@Zaror.cl ", "junta123");

        assertEquals("ok", respuesta.getStatus());
        assertFalse(respuesta.getToken().isBlank());
        assertEquals("Camila Rojas", respuesta.getUsuario().getNombre());
        // El token generado debe ser válido y contener el correo
        assertEquals("vecino@zaror.cl", jwtService.getCorreoFromToken(respuesta.getToken()));
    }

    @Test
    void loginConClaveIncorrectaDevuelveError() {
        when(usuarioRepository.findByCorreo("vecino@zaror.cl")).thenReturn(vecina);

        LoginResponse respuesta = usuarioService.login("vecino@zaror.cl", "otraClave1");

        assertEquals("error", respuesta.getStatus());
        assertNull(respuesta.getUsuario());
    }

    @Test
    void loginConCorreoInexistenteDevuelveError() {
        when(usuarioRepository.findByCorreo("nadie@zaror.cl")).thenReturn(null);

        LoginResponse respuesta = usuarioService.login("nadie@zaror.cl", "junta123");

        assertEquals("error", respuesta.getStatus());
    }

    @Test
    void registrarGuardaVecinoConClaveEncriptada() {
        RegistroRequest datos = new RegistroRequest("Ana Pérez", "12.345.678-5", "ana@correo.cl",
                "987654321", "Los Pinos 55", "clave123");
        when(usuarioRepository.findByCorreo("ana@correo.cl")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        LoginResponse respuesta = usuarioService.registrar(datos);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertEquals("VECINO", guardado.getRol());
        assertNotEquals("clave123", guardado.getClave()); // no se guarda en texto plano
        assertEquals(hashService.sha1("clave123"), guardado.getClave());
        assertEquals("ok", respuesta.getStatus());
        assertEquals(10L, respuesta.getUsuario().getId());
    }

    @Test
    void registrarConCorreoRepetidoLanzaConflicto() {
        RegistroRequest datos = new RegistroRequest("Otra Persona", "12.345.678-5", "vecino@zaror.cl",
                "987654321", "Los Pinos 55", "clave123");
        when(usuarioRepository.findByCorreo("vecino@zaror.cl")).thenReturn(vecina);

        assertThrows(ConflictException.class, () -> usuarioService.registrar(datos));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void actualizarCambiaSoloLosDatosEditables() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vecina));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario actualizado = usuarioService.actualizar(1L,
                new ActualizarUsuarioRequest("Camila Rojas Díaz", "955555555", "Los Aromos 999"));

        assertEquals("Camila Rojas Díaz", actualizado.getNombre());
        assertEquals("955555555", actualizado.getTelefono());
        assertEquals("vecino@zaror.cl", actualizado.getCorreo()); // el correo no cambia
        assertEquals("VECINO", actualizado.getRol());               // el rol no cambia
    }

    @Test
    void obtenerUsuarioInexistenteLanzaNoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    void sha1SiempreEntregaElMismoHash() {
        assertEquals(hashService.sha1("junta123"), hashService.sha1("junta123"));
        assertEquals(40, hashService.sha1("junta123").length());
    }
}
