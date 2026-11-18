package cl.duoc.juntazaror.reservas.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.juntazaror.reservas.dto.DependenciaDTO;
import cl.duoc.juntazaror.reservas.dto.EstadoRequest;
import cl.duoc.juntazaror.reservas.dto.ReservaDTO;
import cl.duoc.juntazaror.reservas.model.Dependencia;
import cl.duoc.juntazaror.reservas.service.ReservaService;
import jakarta.validation.Valid;

/** CRUD de reservas: GET, POST, PUT y DELETE. */
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar() {
        List<ReservaDTO> dtos = reservaService.listar().stream()
                .map(ReservaDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ReservaDTO.fromModel(reservaService.obtenerPorId(id)));
    }

    // Catálogo de dependencias con su capacidad y tarifa
    @GetMapping("/dependencias")
    public ResponseEntity<List<DependenciaDTO>> dependencias() {
        List<DependenciaDTO> dtos = Arrays.stream(Dependencia.values())
                .map(DependenciaDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /reservas/disponibilidad?dependencia=SALON_PRINCIPAL&fecha=2026-12-01&bloque=TARDE
    @GetMapping("/disponibilidad")
    public ResponseEntity<Boolean> disponibilidad(@RequestParam String dependencia, @RequestParam String fecha,
            @RequestParam String bloque) {
        return ResponseEntity.ok(reservaService.estaDisponible(dependencia, reservaService.parsearFecha(fecha), bloque));
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ReservaDTO.fromModel(reservaService.crear(dto, token)));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ReservaDTO> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(ReservaDTO.fromModel(reservaService.cambiarEstado(id, request.getEstado())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
