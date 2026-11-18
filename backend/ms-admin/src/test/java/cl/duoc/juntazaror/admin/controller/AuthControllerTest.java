package cl.duoc.juntazaror.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.juntazaror.admin.dto.LoginResponse;
import cl.duoc.juntazaror.admin.dto.RegistroRequest;
import cl.duoc.juntazaror.admin.dto.UsuarioDTO;
import cl.duoc.juntazaror.admin.exception.ConflictException;
import cl.duoc.juntazaror.admin.exception.GlobalExceptionHandler;
import cl.duoc.juntazaror.admin.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    private final UsuarioDTO usuario = new UsuarioDTO(1L, "Camila Rojas", "11.111.111-1",
            "vecino@zaror.cl", "911111111", "Los Aromos 120", "VECINO");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(usuarioService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void loginCorrecto() throws Exception {
        when(usuarioService.login("vecino@zaror.cl", "junta123"))
                .thenReturn(LoginResponse.ok("token-de-prueba", usuario));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"vecino@zaror.cl\",\"clave\":\"junta123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.token").value("token-de-prueba"))
                .andExpect(jsonPath("$.usuario.rol").value("VECINO"));
    }

    @Test
    void loginIncorrecto() throws Exception {
        when(usuarioService.login("vecino@zaror.cl", "mala"))
                .thenReturn(LoginResponse.error("Correo o contraseña incorrectos"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"vecino@zaror.cl\",\"clave\":\"mala\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void registroConCorreoRepetidoResponde409() throws Exception {
        when(usuarioService.registrar(any(RegistroRequest.class)))
                .thenThrow(new ConflictException("Este correo ya está registrado"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Camila Rojas\",\"rut\":\"11.111.111-1\",\"correo\":\"vecino@zaror.cl\","
                                + "\"telefono\":\"911111111\",\"direccion\":\"Los Aromos 120\",\"clave\":\"junta123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este correo ya está registrado"));
    }
}
