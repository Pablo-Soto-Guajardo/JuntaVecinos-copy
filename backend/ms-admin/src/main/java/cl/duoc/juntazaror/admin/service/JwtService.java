package cl.duoc.juntazaror.admin.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Genera y valida tokens JWT firmados con la clave SECRET (la misma que usa el gateway).
 * El token dura 8 horas (una jornada) para no cortar la sesión durante el uso normal.
 */
@Service
public class JwtService {

    private static final long DURACION_MS = 1000L * 60 * 60 * 8;

    private final SecretKey key;

    public JwtService(@Value("${SECRET:esta_es_mi_compleja_clave_secreta}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String correo, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + DURACION_MS);
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    public String getCorreoFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        return getCorreoFromToken(token) != null;
    }
}
