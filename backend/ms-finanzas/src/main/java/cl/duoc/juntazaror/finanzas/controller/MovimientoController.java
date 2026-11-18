package cl.duoc.juntazaror.finanzas.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.juntazaror.finanzas.dto.MovimientoDTO;
import cl.duoc.juntazaror.finanzas.dto.ResumenDTO;
import cl.duoc.juntazaror.finanzas.service.MovimientoService;
import jakarta.validation.Valid;

/** Sin PUT ni DELETE a propósito: los movimientos no se modifican (trazabilidad). */
@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> listar() {
        List<MovimientoDTO> dtos = movimientoService.listar().stream()
                .map(MovimientoDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenDTO> resumen() {
        return ResponseEntity.ok(movimientoService.calcularResumen(movimientoService.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(MovimientoDTO.fromModel(movimientoService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrar(@Valid @RequestBody MovimientoDTO dto) {
        return ResponseEntity.ok(MovimientoDTO.fromModel(movimientoService.registrar(dto)));
    }
}
