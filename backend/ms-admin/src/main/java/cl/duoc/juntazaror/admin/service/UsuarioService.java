package cl.duoc.juntazaror.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.duoc.juntazaror.admin.dto.ActualizarUsuarioRequest;
import cl.duoc.juntazaror.admin.dto.LoginResponse;
import cl.duoc.juntazaror.admin.dto.RegistroRequest;
import cl.duoc.juntazaror.admin.dto.UsuarioDTO;
import cl.duoc.juntazaror.admin.exception.ConflictException;
import cl.duoc.juntazaror.admin.exception.ResourceNotFoundException;
import cl.duoc.juntazaror.admin.model.Usuario;
import cl.duoc.juntazaror.admin.repository.UsuarioRepository;

/** Reglas de negocio de usuarios: login, registro y edición de perfil. */
@Service
public class UsuarioService {

    public static final String ROL_VECINO = "VECINO";

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final HashService hashService;

    public UsuarioService(UsuarioRepository usuarioRepository, JwtService jwtService, HashService hashService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
    }

    /** Compara el hash de la clave ingresada con el guardado. Si coincide entrega un token. */
    public LoginResponse login(String correo, String clave) {
        String correoNormalizado = correo == null ? "" : correo.trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByCorreo(correoNormalizado);
        if (usuario == null || clave == null || !hashService.sha1(clave).equals(usuario.getClave())) {
            logger.warn("Login fallido para {}", correoNormalizado);
            return LoginResponse.error("Correo o contraseña incorrectos");
        }
        logger.info("Login correcto para {}", correoNormalizado);
        String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRol());
        return LoginResponse.ok(token, UsuarioDTO.fromModel(usuario));
    }

    /** Regla de negocio: no pueden existir dos cuentas con el mismo correo. Siempre crea un VECINO. */
    public LoginResponse registrar(RegistroRequest datos) {
        String correo = datos.getCorreo().trim().toLowerCase();
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new ConflictException("Este correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setRut(datos.getRut().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(datos.getTelefono().trim());
        usuario.setDireccion(datos.getDireccion().trim());
        usuario.setClave(hashService.sha1(datos.getClave()));
        usuario.setRol(ROL_VECINO);

        Usuario guardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado id={}", guardado.getId());
        String token = jwtService.generateToken(guardado.getCorreo(), guardado.getRol());
        return LoginResponse.ok(token, UsuarioDTO.fromModel(guardado));
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no existe"));
    }

    public Usuario actualizar(Long id, ActualizarUsuarioRequest datos) {
        Usuario existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre().trim());
        existente.setTelefono(datos.getTelefono().trim());
        existente.setDireccion(datos.getDireccion().trim());
        return usuarioRepository.save(existente);
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }
}
