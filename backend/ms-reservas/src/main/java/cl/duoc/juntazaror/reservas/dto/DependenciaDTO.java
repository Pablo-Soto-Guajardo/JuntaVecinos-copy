package cl.duoc.juntazaror.reservas.dto;

import cl.duoc.juntazaror.reservas.model.Dependencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependenciaDTO {
    private String codigo;
    private String nombre;
    private Integer capacidad;
    private Long tarifaPorBloque;

    public static DependenciaDTO fromModel(Dependencia d) {
        return new DependenciaDTO(d.name(), d.getNombre(), d.getCapacidad(), d.getTarifaPorBloque());
    }
}
