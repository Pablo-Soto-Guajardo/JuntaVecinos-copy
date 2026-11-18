package cl.duoc.juntazaror.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.juntazaror.admin.dto.LoginRequest;
import cl.duoc.juntazaror.admin.dto.LoginResponse;
import cl.duoc.juntazaror.admin.dto.RegistroRequest;
import cl.duoc.juntazaror.admin.service.UsuarioService;
import jakarta.validation.Valid;

/** Endpoints públicos (el gateway los deja pasar sin token). */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /auth/login  { "correo": "...", "clave": "..." }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request.getCorreo(), request.getClave()));
    }

    // POST /auth/register  { nombre, rut, correo, telefono, direccion, clave }
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(usuarioService.registrar(request));
    }
}
