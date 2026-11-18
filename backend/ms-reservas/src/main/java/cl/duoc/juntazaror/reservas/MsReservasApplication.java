package cl.duoc.juntazaror.reservas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/** ms-reservas: arriendo de dependencias y control de disponibilidad (puerto 9092). */
@SpringBootApplication
public class MsReservasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsReservasApplication.class, args);
	}

	// Cliente HTTP para consultar a otros microservicios a través del api-gateway
	@Bean
	public WebClient webClient(@Value("${api.base-url}") String baseUrl) {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.build();
	}
}
