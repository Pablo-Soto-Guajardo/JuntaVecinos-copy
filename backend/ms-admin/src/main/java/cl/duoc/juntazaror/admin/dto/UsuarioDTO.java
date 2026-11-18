package cl.duoc.juntazaror.admin.dto;

import cl.duoc.juntazaror.admin.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos públicos del usuario (sin la clave). Es lo que recibe la app móvil. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
    private String direccion;
    private String rol;

    public static UsuarioDTO fromModel(Usuario u) {
        if (u == null) return null;
        return new UsuarioDTO(u.getId(), u.getNombre(), u.getRut(), u.getCorreo(),
                u.getTelefono(), u.getDireccion(), u.getRol());
    }
}
