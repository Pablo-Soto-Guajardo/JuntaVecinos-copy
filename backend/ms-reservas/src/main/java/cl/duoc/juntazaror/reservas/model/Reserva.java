package cl.duoc.juntazaror.reservas.model;

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
 * Tabla "reserva".
 * dependencia: SALON_PRINCIPAL | SALA_REUNIONES | MULTICANCHA
 * bloque:      MANANA | TARDE | NOCHE
 * estado:      PENDIENTE | CONFIRMADA | RECHAZADA
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 80)
    private String nombreSolicitante;

    @Column(nullable = false, length = 30)
    private String dependencia;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 10)
    private String bloque;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Column(nullable = false)
    private Integer asistentes;

    @Column(nullable = false)
    private Long monto;

    @Column(nullable = false, length = 15)
    private String estado;
}
