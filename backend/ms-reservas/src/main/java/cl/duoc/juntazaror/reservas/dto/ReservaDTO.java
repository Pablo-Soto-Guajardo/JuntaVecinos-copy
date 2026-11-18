package cl.duoc.juntazaror.reservas.dto;

import cl.duoc.juntazaror.reservas.model.Reserva;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos que viajan entre la app y el servidor.
 * La fecha viaja como texto "yyyy-MM-dd". monto y estado los calcula el servidor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El nombre del solicitante es obligatorio")
    private String nombreSolicitante;

    @NotBlank(message = "La dependencia es obligatoria")
    private String dependencia;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "El bloque horario es obligatorio")
    private String bloque;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 10, max = 200, message = "El motivo debe tener entre 10 y 200 caracteres")
    private String motivo;

    @NotNull(message = "La cantidad de asistentes es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 asistente")
    private Integer asistentes;

    private Long monto;

    private String estado;

    public static ReservaDTO fromModel(Reserva r) {
        if (r == null) return null;
        return new ReservaDTO(r.getId(), r.getUsuarioId(), r.getNombreSolicitante(), r.getDependencia(),
                r.getFecha().toString(), r.getBloque(), r.getMotivo(), r.getAsistentes(), r.getMonto(),
                r.getEstado());
    }
}
