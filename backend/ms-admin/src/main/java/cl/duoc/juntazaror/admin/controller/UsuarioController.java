package cl.duoc.juntazaror.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.juntazaror.admin.dto.ActualizarUsuarioRequest;
import cl.duoc.juntazaror.admin.dto.UsuarioDTO;
import cl.duoc.juntazaror.admin.service.UsuarioService;
import jakarta.validation.Valid;

/** Endpoints protegidos: el gateway exige el token JWT. */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> dtos = usuarioService.listar().stream()
                .map(UsuarioDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioDTO.fromModel(usuarioService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(UsuarioDTO.fromModel(usuarioService.actualizar(id, request)));
    }

    // Lo usa ms-reservas para validar que el solicitante existe
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existePorId(id));
    }
}
