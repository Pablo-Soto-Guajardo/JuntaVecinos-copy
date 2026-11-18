package cl.duoc.juntazaror.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** ms-finanzas: ingresos, gastos y comprobantes de la junta (puerto 9093). */
@SpringBootApplication
public class MsFinanzasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsFinanzasApplication.class, args);
	}
}
