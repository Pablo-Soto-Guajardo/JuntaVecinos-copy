package cl.duoc.juntazaror.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos del formulario de registro de la app (siempre crea un VECINO). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y comenzar con 9")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String clave;
}
