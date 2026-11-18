package cl.duoc.juntazaror.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.juntazaror.admin.model.Usuario;
import cl.duoc.juntazaror.admin.repository.UsuarioRepository;
import cl.duoc.juntazaror.admin.service.HashService;

/**
 * Carga los 3 usuarios de prueba la primera vez (datos FICTICIOS).
 * Contraseña de todos: junta123
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final HashService hashService;

    DataLoader(UsuarioRepository usuarioRepository, HashService hashService) {
        this.usuarioRepository = usuarioRepository;
        this.hashService = hashService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return;
        String clave = hashService.sha1("junta123");
        usuarioRepository.save(new Usuario(null, "Camila Rojas", "11.111.111-1", "vecino@zaror.cl",
                "911111111", "Los Aromos 120", clave, "VECINO"));
        usuarioRepository.save(new Usuario(null, "Jorge Muñoz", "12.345.678-5", "directiva@zaror.cl",
                "922222222", "Los Jazmines 425", clave, "DIRECTIVA"));
        usuarioRepository.save(new Usuario(null, "Patricia Soto", "13.131.313-6", "tesoreria@zaror.cl",
                "933333333", "Las Violetas 88", clave, "TESORERIA"));
    }
}
