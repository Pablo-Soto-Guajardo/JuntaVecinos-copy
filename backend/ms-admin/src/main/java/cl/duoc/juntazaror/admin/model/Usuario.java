package cl.duoc.juntazaror.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabla "usuario". La clave NUNCA se guarda en texto plano: se guarda su hash SHA-1.
 * Roles posibles: VECINO, DIRECTIVA, TESORERIA.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 15)
    private String rut;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 12)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String direccion;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = false, length = 20)
    private String rol;
}
