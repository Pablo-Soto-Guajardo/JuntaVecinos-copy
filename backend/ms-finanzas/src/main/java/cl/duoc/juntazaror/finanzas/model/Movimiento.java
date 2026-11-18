package cl.duoc.juntazaror.finanzas.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabla "movimiento". Los movimientos NO se eliminan ni se editan:
 * así se mantiene la trazabilidad de la rendición de cuentas.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String categoria;

    @Column(nullable = false)
    private Long monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 150)
    private String descripcion;

    // Referencia a la foto del comprobante tomada en el teléfono
    @Column(length = 500)
    private String comprobanteUri;

    @Column(nullable = false, length = 80)
    private String registradoPor;
}
