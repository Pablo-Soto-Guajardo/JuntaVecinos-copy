package cl.duoc.juntazaror.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Transparencia: total de ingresos, gastos y saldo disponible. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDTO {
    private Long totalIngresos;
    private Long totalGastos;
    private Long saldo;
}
