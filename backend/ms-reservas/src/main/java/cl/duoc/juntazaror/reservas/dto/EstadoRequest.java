package cl.duoc.juntazaror.reservas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de PUT /reservas/{id}/estado  { "estado": "CONFIRMADA" } */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRequest {
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
