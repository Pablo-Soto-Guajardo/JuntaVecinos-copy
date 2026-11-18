package cl.duoc.juntazaror.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de /auth/login y /auth/register.
 * status = "ok" (con token y usuario) o "error" (con mensaje).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String status;
    private String token;
    private String mensaje;
    private UsuarioDTO usuario;

    public static LoginResponse ok(String token, UsuarioDTO usuario) {
        return new LoginResponse("ok", token, "Sesión iniciada", usuario);
    }

    public static LoginResponse error(String mensaje) {
        return new LoginResponse("error", "", mensaje, null);
    }
}
