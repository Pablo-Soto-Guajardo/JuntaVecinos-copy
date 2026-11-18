package cl.duoc.juntazaror.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway: único punto de entrada para la app móvil (puerto 9090).
 * Valida el token JWT y redirige cada petición al microservicio que corresponde.
 */
@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
