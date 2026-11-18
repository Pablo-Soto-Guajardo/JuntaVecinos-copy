package cl.duoc.juntazaror.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** ms-admin: usuarios, roles y autenticación con JWT (puerto 9091). */
@SpringBootApplication
public class MsAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAdminApplication.class, args);
	}
}
