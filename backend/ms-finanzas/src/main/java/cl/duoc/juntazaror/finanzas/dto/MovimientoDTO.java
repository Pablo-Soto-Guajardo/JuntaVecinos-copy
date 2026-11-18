package cl.duoc.juntazaror.finanzas.dto;

import cl.duoc.juntazaror.finanzas.model.Movimiento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** La fecha viaja como texto "yyyy-MM-dd". */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Long id;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    @Max(value = 10000000, message = "El monto no puede superar $10.000.000")
    private Long monto;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres")
    private String descripcion;

    private String comprobanteUri;

    @NotBlank(message = "Se debe indicar quién registra el movimiento")
    private String registradoPor;

    public static MovimientoDTO fromModel(Movimiento m) {
        if (m == null) return null;
        return new MovimientoDTO(m.getId(), m.getTipo(), m.getCategoria(), m.getMonto(),
                m.getFecha().toString(), m.getDescripcion(), m.getComprobanteUri(), m.getRegistradoPor());
    }
}
